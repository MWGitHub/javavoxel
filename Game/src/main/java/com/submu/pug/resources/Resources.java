package com.submu.pug.resources;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.submu.pug.data.ConfigData;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

/**
 * Loads and unloads resources from the file system.
 * @author MW
 */
public class Resources {
    /**
     * Initializes the resources and sets the location of the program's root.
     */
    public Resources() {
    }

    /**
     * Loads data into the resources cache.
     * The data is converted to a string when placed in the cache.
     * @param path the path of the file to load relative to the program.
     * @throws IOException occurs when the file is not found.
     * @return the loaded data as a string.
     */
    public String loadTextFile(String path) throws IOException {
        File file = new File(path);

        // Convert the file to a string.
        String output;
        try {
            output = Files.toString(file, Charsets.UTF_8);
        } catch (IOException ex) {
            LoggerFactory.getLogger(Resources.class).error("Unable to locate the resource at " + path + ".", ex);
            throw ex;
        }

        return output;
    }

    /**
     * Attempts to load the configuration file.
     * If no config file is found then one is created.
     * If no path is given then it will not save the config file to disk.
     * @param path the path of the config file.
     * @return the config data.
     */
    public ConfigData loadConfigFile(String path) {
        ConfigData config = null;
        if (path != null) {
            try {
                String data = loadTextFile(path);
                config = Resources.loadJson(data, ConfigData.class);
            } catch (IOException e) {
                LoggerFactory.getLogger(Resources.class).error("Unable to load the config file at "
                        + path + ", creating a default config file.", e);
            }
            // Create a new config file and save it if none exists.
            if (config == null) {
                config = new ConfigData();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String outputData = gson.toJson(config, ConfigData.class);
                try {
                    writeData(outputData, path);
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }
        } else {
            config = new ConfigData();
        }

        return config;
    }

    /**
     * Writes data to the file system.
     * @param data the data to write.
     * @param path the path to write to.
     * @throws IOException thrown when the file is not written.
     */
    public static void writeData(String data, String path) throws IOException {
        try {
            Files.write(data, new File(path), Charsets.UTF_8);
        } catch (IOException ex) {
            LoggerFactory.getLogger(Resources.class).error("Unable to write file to " + path + ".", ex);
            throw ex;
        }
    }

    /**
     * Parses data from a string.
     * @param input the input string to parse.
     * @param classOfT the class of the Json data.
     * @param <T> the class of the Json data.
     * @return the parsed Json data.
     */
    public static <T> T loadJson(String input, Class<T> classOfT) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(input, classOfT);
    }
}
