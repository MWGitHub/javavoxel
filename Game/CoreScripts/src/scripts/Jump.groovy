package scripts

import com.exploringlines.entitysystem.Entity
import com.jme3.math.Vector3f
import core.EventFunctions
import core.ScriptBindings

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/10/13
 * Time: 3:18 PM
 * Basic jumping script.
 */
class Jump {
    ScriptBindings bindings

    Jump(ScriptBindings bindings) {
        this.bindings = bindings

        // Make the entity jump if on the floor.
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_CAST, new EventFunctions() {
            @Override
            void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {
                if (internalName != "Jump") return
                if (!bindings.entityBindings.isEntityOnFloor(entity)) return
                Vector3f currentAcceleration = bindings.entityBindings.getAcceleration(entity)
                bindings.entityBindings.setAcceleration(entity,
                        currentAcceleration.x, bindings.entityBindings.getJumpPower(entity), currentAcceleration.y)
            }
        })
    }
}
