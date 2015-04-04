package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/28/13
 * Time: 2:18 PM
 */
public class DataComponent implements Component {
    /**
     * Holds arbitrary user data in string form.
     */
    public Map<String, String> data = new HashMap<String, String>();

    @Override
    public Component copy() {
        DataComponent output = new DataComponent();
        output.data.putAll(data);

        return output;
    }
}
