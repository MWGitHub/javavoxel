package com.halboom.pgt.physics.debug;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/2/13
 * Time: 1:40 PM
 * Component to enable showing of debug bounds.
 */
public class DebugBoundsComponent implements Component {
    @Override
    public Component copy() {
        return new DebugBoundsComponent();
    }
}
