package com.submu.pug.application;

import com.jme3.system.AppSettings;
import com.submu.pug.data.ConfigData;
import com.submu.pug.data.Data;
import com.submu.pug.data.PathNames;
import com.submu.pug.editor.EditorState;
import com.submu.pug.play.PlayState;
import com.submu.pug.resources.LoadState;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/12/13
 * Time: 2:58 PM
 * Creates a launcher form and adds the JMonkey context to it.
 */
public class Launcher {
    /**
     * Main frame of the application.
     */
    private JFrame frame;

    /**
     * Initializes and creates the launcher.
     */
    public Launcher() {
        UISkin.activateSkin();

        // Create the launcher window.
        frame = new JFrame("Game Launcher");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        LauncherPanel launcherPanel = new LauncherPanel();
        launcherPanel.setEditorActionCommand(new LauncherCallback() {
            @Override
            public void execute() {
                createEditor();
            }
        });
        launcherPanel.setGameActionCommand(new LauncherCallback() {
            @Override
            public void execute() {
                createGame();
            }
        });
        frame.add(launcherPanel);

        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Skips directly to the editor without the startup launcher.
     */
    public void skipToEditor() {
        createEditor();
    }

    /**
     * Retrieves configuration settings.
     * @return the configuration settings to retrieve.
     * @param forceWindow true to force window mode only.
     */
    private AppSettings getConfigSettings(boolean forceWindow) {
        ConfigData config = Data.getInstance().getResources().loadConfigFile(
                Data.getInstance().getResourcePaths().getPath(PathNames.CONFIG_FILE));
        Data.getInstance().setConfigData(config);

        AppSettings settings = null;
        // Set config if settings are not shown.
        if (!config.showSettings) {
            settings = new AppSettings(true);
            settings.setResolution(config.graphics.resolution.width, config.graphics.resolution.height);
            if (forceWindow) {
                settings.setFullscreen(false);
            } else {
                if (config.graphics.fullscreen == ConfigData.Graphics.SCREEN_FULL) {
                    settings.setFullscreen(true);
                } else {
                    settings.setFullscreen(false);
                    if (config.graphics.fullscreen == ConfigData.Graphics.SCREEN_BORDERLESS) {
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                    }
                }
            }
            settings.setVSync(config.graphics.vsync);
            settings.setUseInput(true);
            settings.setFrameRate(config.graphics.framerate);
            settings.setBitsPerPixel(config.graphics.bitsPerPixel);
            settings.setSamples(config.graphics.samples);
            settings.setRenderer(AppSettings.LWJGL_OPENGL2);
            settings.setTitle("Game");
        }

        return settings;
    }

    /**
     * Creates the game.
     */
    private void createGame() {
        // Close the launcher.
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.dispose();

        AppSettings settings = getConfigSettings(false);
        if (settings != null) {
            settings.setTitle("Game");
        }
        new MonkeyPug(new LoadState(new PlayState()), settings);
    }

    /**
     * Creates the editor.
     */
    private void createEditor() {
        // Close the launcher.
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.dispose();

        AppSettings settings = getConfigSettings(true);
        // Create the editor frame.
        JFrame editorFrame = new JFrame("Game Editor");
        // Exit the program when the main active frame is closed.
        editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        editorFrame.pack();

        // Create the jMonkey context and start the framework.
        try {
            if (settings != null) {
                settings.setTitle("Game Editor");
            }
            new MonkeyPug(editorFrame, new LoadState(new EditorState(editorFrame)), settings);
        } catch (Exception e) {
            LoggerFactory.getLogger(Launcher.class).error("Error opening the editor window.", e);
            System.exit(1);
        }
    }
}
