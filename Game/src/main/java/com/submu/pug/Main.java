package com.submu.pug;

import com.halboom.pgt.debug.LogExceptionHandler;
import com.halboom.pgt.resources.ResourcePaths;
import com.submu.pug.application.Launcher;
import com.submu.pug.data.Data;
import com.submu.pug.data.PathNames;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 * Main class for the application.
 */
public final class Main {
    /**
     * Do not allow instantiation of this class.
     */
    private Main() {

    }

    /**
     * Sets up the paths used for the game.
     */
    private static void setUpConfigPaths() {
        ResourcePaths paths = Data.getInstance().getResourcePaths();
        paths.addToken("$user.dir$", System.getProperty("user.dir"));
        paths.addToken("$APPDATA$", System.getenv("APPDATA"));
        paths.addToken("$user.home$", System.getProperty("user.home"));
        if (ResourcePaths.isDirectoryWritable(System.getProperty("user.dir"))) {
            paths.addPath(PathNames.CONFIG_FILE, "$user.dir$/Config.json");
        } else if (ResourcePaths.isDirectoryWritable(System.getenv("APPDATA") + "/PugGame")) {
            paths.addPath(PathNames.CONFIG_FILE, "$APPDATA$/PugGame/Config.json");
        } else {
            LoggerFactory.getLogger(Main.class).error("Unable to locate a config file. Make " +
                    "sure the directory is writable.");
        }
    }

    /**
     * Starts the program.
     * @param args the arguments to pass in.
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("logger.properties");
        org.apache.log4j.Logger.getLogger("com.jme3").setLevel(Level.ERROR);
        Thread.setDefaultUncaughtExceptionHandler(new LogExceptionHandler());

        setUpConfigPaths();

        Launcher launcher = new Launcher();
        //launcher.skipToEditor();
    }
}

