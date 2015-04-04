package com.halboom.pgt.asseteditor;

import com.alee.laf.WebLookAndFeel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main launcher to test the library with.
 */
public final class App {
    /**
     * Prevent instantiation.
     */
    private App() {
    }

    /**
     * Starts the test application.
     * @param args the arguments to pass in.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WebLookAndFeel.install();

                String testTemp = System.getenv("APPDATA") + "/PugGame/Temp/";
                AssetImporter assetImporter = new AssetImporter(testTemp, "Assets/");
                assetImporter.setCloseOperation(JFrame.EXIT_ON_CLOSE);
                assetImporter.show();
            }
        });
    }
}
