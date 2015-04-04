package com.submu.pug.camera;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/17/13
 * Time: 12:38 PM
 * Component for the chase camera.
 */
public class ChaseCameraComponent implements Component {
    @Override
    public Component copy() {
        return new ChaseCameraComponent();
    }
}
