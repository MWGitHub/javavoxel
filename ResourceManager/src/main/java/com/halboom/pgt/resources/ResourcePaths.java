package com.halboom.pgt.resources;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/9/13
 * Time: 6:15 PM
 * Stores paths to be used when modifying resources.
 */
public class ResourcePaths {
    /**
     * Unparsed paths for the resources.
     */
    private Map<String, String> paths = new HashMap<String, String>();

    /**
     * Tokens to replace in parsed paths.
     */
    private Map<String, String> tokens = new HashMap<String, String>();

    /**
     * Initializes the default paths.
     */
    public ResourcePaths() {
    }

    /**
     * Stores the path given the key and a value.
     * @param key the key to refer to the path.
     * @param value the value of the path.
     */
    public void addPath(String key, String value) {
        paths.put(key, value);
    }

    /**
     * Retrieves a path given a key.
     * If the path has tokens it will be parsed.
     * @param key the key of the path.
     * @return the parsed path, null if the key does not exist.
     */
    public String getPath(String key) {
        if (paths.containsKey(key)) {
            return parsePath(paths.get(key));
        }
        return null;
    }

    /**
     * Retrieves the unparsed path given a key.
     * @param key the key of the path.
     * @return the unparsed path.
     */
    public String getRawPath(String key) {
        if (paths.containsKey(key)) {
            return paths.get(key);
        }
        return null;
    }

    /**
     * Removes a path.
     * @param key the key of the path.
     */
    public void removePath(String key) {
        paths.remove(key);
    }

    /**
     * Adds a token to the tokens.
     * @param token the token to store.
     * @param parsed the parsed output from the token.
     */
    public void addToken(String token, String parsed) {
        tokens.put(token, parsed);
    }

    /**
     * Removes a token from the tokens.
     * @param token the token to remove.
     */
    public void removeToken(String token) {
        tokens.remove(token);
    }

    /**
     * Parses a path.
     * @param input the input to parse.
     * @return the output string based with tokens replaced.
     */
    private String parsePath(String input) {
        String output = input;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }

    /**
     * Checks if a directory is writable.
     * @param path the path to check.
     * @return true if writable.
     */
    public static boolean isDirectoryWritable(String path) {
        File file = new File(path + "/writable,test");
        try {
            Files.createParentDirs(file);
        } catch (IOException e) {
            return false;
        }
        try {
            Files.touch(file);
        } catch (IOException ex) {
            return false;
        }

        // Delete the writable file and if not deletable return false.
        return file.delete();
    }
}
