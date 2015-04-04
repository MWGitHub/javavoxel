package com.submu.pug.application;

import com.halboom.pgt.pgutil.threading.Threading;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.audio.AudioContext;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.awt.AwtPanel;
import com.jme3.system.awt.AwtPanelsContext;
import com.jme3.system.awt.PaintMode;
import com.submu.pug.data.ConfigData;
import com.submu.pug.data.Data;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/12/13
 * Time: 5:15 PM
 * Main swingContext creator for the game and editor.
 */
public class MonkeyPug extends SimpleApplication {
    /**
     * True to use an awt panel instead of a swing panel.
     */
    private boolean useAWT = true;

    /**
     * State to switch to when setting up is complete.
     */
    private AbstractAppState initialState;

    /**
     * Context of the canvas.
     */
    private JmeCanvasContext swingContext;

    /**
     * Jmonkey canvas.
     */
    private Canvas canvas;

    /**
     * Panel used for the objects.
     */
    private AwtPanel rootPanel;

    /**
     * MainLoop to check for application updating.
     */
    private MainLoop mainLoop;

    /**
     * Initializes the application without swing and with default settings.
     * @param initialState the initial state to switch to.
     * @param settings the settings of the application.
     */
    public MonkeyPug(AbstractAppState initialState, AppSettings settings) {
        useAWT = false;
        this.initialState = initialState;

        if (settings != null) {
            setSettings(settings);
            showSettings = false;
        } else {
            setSettings(new AppSettings(true));
            showSettings = true;
        }
        setPauseOnLostFocus(false);
        Thread jme = new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
        jme.start();
    }

    /**
     * Initializes and creates the jMonkey application.
     * @param frame the frame to attach to.
     * @param initialState the initial state to switch to when loading is complete.
     * @param settings the settings of the application.
     */
    public MonkeyPug(JFrame frame, AbstractAppState initialState, AppSettings settings) {
        this.initialState = initialState;

        // Close jMonkey when the main window is closed.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stop();
            }
        });
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        // Create the canvas for jMonkey.
        if (settings != null) {
            if (useAWT) {
                createAwtContext(frame, settings);
            } else {
                settings.setRenderer(AppSettings.LWJGL_OPENGL2);
                createCanvas(settings);
            }
        } else {
            AppSettings newSettings = new AppSettings(true);
            if (useAWT) {
                createAwtContext(frame, newSettings);
            } else {
                newSettings.setRenderer(AppSettings.LWJGL_OPENGL2);
                createCanvas(newSettings);
            }
        }
        if (!useAWT) {
            frame.add(canvas, BorderLayout.CENTER);
        }
        // Start the application.
        startApp();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Starts the application.
     */
    private void startApp() {
        enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
            getFlyByCamera().setDragToRotate(true);
            return null;
            }
        });
    }

    /**
     * Creates an AWT panel context.
     * @param frame the frame to add to.
     * @param settings the settings of the application.
     */
    private void createAwtContext(JFrame frame, AppSettings settings) {
        setPauseOnLostFocus(false);
        setShowSettings(false);
        settings.setCustomRenderer(AwtPanelsContext.class);
        setSettings(settings);
        start();

        final JFrame panelFrame = frame;
        final AppSettings settingsFinal = settings;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AwtPanelsContext context = (AwtPanelsContext) getContext();
                rootPanel = context.createPanel(PaintMode.Accelerated);
                rootPanel.setPreferredSize(new Dimension(settingsFinal.getWidth(), settingsFinal.getHeight()));
                context.setInputSource(rootPanel);

                panelFrame.getContentPane().setLayout(new BorderLayout());
                panelFrame.getContentPane().add(rootPanel, BorderLayout.CENTER);

                panelFrame.pack();
                panelFrame.setLocationRelativeTo(null);
                panelFrame.setVisible(true);
            }
        });
    }

    /**
     * Creates the application canvas.
     * @param settings the settings to create the app with.
     */
    private void createCanvas(AppSettings settings) {
        setPauseOnLostFocus(false);
        setSettings(settings);
        createCanvas();
        startCanvas();

        swingContext = (JmeCanvasContext) getContext();
        canvas = swingContext.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }

    /**
     * Initialize the application.
     */
    @Override
    public void simpleInitApp() {
        if (useAWT) {
            rootPanel.attachTo(true, viewPort, guiViewPort);
            guiViewPort.setClearFlags(false, true, true);
        }

        ConfigData config = Data.getInstance().getConfigData();
        mainLoop = new MainLoop(config.logicRate, config.graphics.framerate);
        mainLoop.enable();

        getStateManager().attach(initialState);
    }

    @Override
    /**
     * Split the renderer and the logic into different threads.
     */
    public void update() {
        // Make sure the audio renderer is available to callables
        AudioContext.setAudioRenderer(audioRenderer);

        runQueuedTasks();

        if (speed == 0 || paused) {
            return;
        }

        // Update the logic
        mainLoop.update();
        while (mainLoop.shouldUpdateLogic()) {
            float tpf = mainLoop.getLogicStepSize() * speed;

            if (inputEnabled) {
                inputManager.update(tpf);
            }

            if (audioRenderer != null) {
                audioRenderer.update(tpf);
            }

            if (speed == 0 || paused) {
                return;
            }

            // update states
            stateManager.update(tpf);

            // simple update and root node
            simpleUpdate(tpf);

            rootNode.updateLogicalState(tpf);
            guiNode.updateLogicalState(tpf);
        }


        // Renderer may already be threaded.
        // Update the display as many times as possible based on the framerate
        timer.update();
        float tpf = timer.getTimePerFrame() * speed;
        // Update the fps when the display updates to make it easier to read.
        if (mainLoop.shouldUpdateDisplay()) {
            fpsText.setText("FPS: " + (int) (1f / tpf));
        }

        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        simpleRender(renderManager);
        stateManager.postRender();
    }

    @Override
    public void destroy() {
        super.destroy();
        Threading.getInstance().destroy();
    }
}
