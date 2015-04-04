package com.submu.pug.application;

import com.alee.laf.WebLookAndFeel;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/28/13
 * Time: 5:50 PM
 * Skins the Nimbus UI.
 */
public final class UISkin {
    /**
     * Constructs the skins.
     */
    private UISkin() {

    }

    /**
     * Activates the nimbus skin.
     */
    private static void activateNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(UISkin.class).warn("Unable to set the look and feel, using default.", e);
        }

        UIManager.put("control", new ColorUIResource(0x333333));
        UIManager.put("text", new ColorUIResource(0x0));
        UIManager.put("nimbusDisabledText", new ColorUIResource(0x8e8f91));
        UIManager.put("nimbusLightBackground", new ColorUIResource(0xffffff));
        UIManager.put("nimbusSelectionBackground", new ColorUIResource(0x39698a));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(0x656565));
        UIManager.put("text", new ColorUIResource(0xcccccc));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(0x333333));
        UIManager.put("nimbusBase", new ColorUIResource(0x33628c));
        UIManager.put("nimbusSelectionBackground", new ColorUIResource(0x3399ff));
        UIManager.put("nimbusLightBackground", new ColorUIResource(0x333333));
    }

    /**
     * Activates the web look and feel.
     */
    private static void activateWebLAF() {
        WebLookAndFeel.install();
    }

    /**
     * Themes the skin.
     */
    public static void activateSkin() {
        activateWebLAF();
    }
}
