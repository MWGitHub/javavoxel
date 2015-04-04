package com.submu.pug.game.objects;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.halboom.pgt.entityspatial.AnimationComponent;
import com.halboom.pgt.entityspatial.ModelComponent;
import com.halboom.pgt.entityspatial.SpatialComponent;
import com.halboom.pgt.entityspatial.SpatialSystem;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.physics.bullet.components.BulletComponent;
import com.halboom.pgt.physics.components.PhysicsStateComponent;
import com.halboom.pgt.physics.debug.DebugBoundsComponent;
import com.halboom.pgt.physics.filters.ColliderHistoryComponent;
import com.halboom.pgt.physics.filters.PulseCollisionComponent;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.GridColliderComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.submu.pug.camera.CameraComponent;
import com.submu.pug.camera.ChaseCameraComponent;
import com.submu.pug.data.Data;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.game.actions.ChaseComponent;
import com.submu.pug.game.objects.components.*;
import com.submu.pug.game.world.GravityComponent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/6/13
 * Time: 4:35 PM
 * Creates objects given the data.
 */
public class GameObjectFactory {
    /**
     * Key for the default actor.
     */
    public static final String DEFAULT_ACTOR = "com.halboom.pug.defaultactor";

    /**
     * Entity system to create entities with.
     */
    private EntitySystem entitySystem;

    /**
     * Spatial system to use with entities.
     */
    private SpatialSystem spatialSystem;

    /**
     * Lookup table for components that will work even after obfuscation.
     */
    private static Map<String, Class<? extends Component>> componentTable = createComponentTable();

    /**
     * Map to store object information.
     */
    private Map<String, List<Component>> actors = new HashMap<String, List<Component>>();

    /**
     * Callbacks for object creation.
     */
    private ActorCallbacks callbacks;

    /**
     * Initializes the GameObjectFactory.
     * @param entitySystem the entity system to create entities with.
     * @param spatialSystem the spatial system to use with entities.
     * @param objectsData the data to load objects with.
     */
    public GameObjectFactory(EntitySystem entitySystem, SpatialSystem spatialSystem, ObjectsData objectsData) {
        this.entitySystem = entitySystem;
        this.spatialSystem = spatialSystem;

        // Load the actors into the data map using intermediate data.
        Map<String, ObjectsData.ActorData> actorData = objectsData.actors;
        for (Map.Entry<String, ObjectsData.ActorData> actor : actorData.entrySet()) {
            actors.put(actor.getKey(), actor.getValue().parsedComponents);
        }

        actors.put(DEFAULT_ACTOR, actors.get(objectsData.defaultActor));
    }

    /**
     * Parses the object data.
     * @param data the data to parse.
     * @return the parsed object data.
     */
    public static ObjectsData parseObjectsData(String data) {
        Gson gson = new GsonBuilder().create();

        // Parse the components manually.
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectsData objectsData = null;
        try {
            // Get the actors.
            objectsData = mapper.readValue(data, ObjectsData.class);
            JsonNode root = mapper.readTree(data);
            JsonNode actors = root.with("actors");
            Iterator<Map.Entry<String, JsonNode>> actorIter = actors.fields();
            // Iterate through the actors.
            while (actorIter.hasNext()) {
                // Create the prototype for the actor.
                Map.Entry<String, JsonNode> actorRoot = actorIter.next();
                ObjectsData.ActorData actorData = objectsData.actors.get(actorRoot.getKey());
                JsonNode components = actorRoot.getValue().get("components");
                Iterator<Map.Entry<String, JsonNode>> componentsIter = components.fields();
                // Create each component with the correct type.
                while (componentsIter.hasNext()) {
                    Map.Entry<String, JsonNode> componentPair = componentsIter.next();
                    // Remove ending numbers on components to allow for multiple of the same component.
                    String componentName = componentPair.getKey().split("[0-9]+$")[0];
                    String componentData = componentPair.getValue().toString();
                    Class componentClass = componentTable.get(componentPair.getKey());
                    // Load the component with the correct type.
                    if (componentClass != null) {
                        // Set the values for the components.
                        Component component = gson.fromJson(componentData, componentTable.get(componentName));
                        actorData.parsedComponents.add(component);
                    }
                }
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(GameObjectFactory.class).error("Unable to load the map's object data.", e);
        }

        return objectsData;
    }

    /**
     * Creates the component table.
     */
    private static Map<String, Class<? extends Component>> createComponentTable() {
        componentTable = new HashMap<String, Class<? extends Component>>();
        componentTable.put("AABBComponent", AABBComponent.class);
        componentTable.put("CollisionComponent", CollisionComponent.class);
        componentTable.put("AbilityComponent", AbilityComponent.class);
        componentTable.put("ActorComponent", ActorComponent.class);
        componentTable.put("AnimationComponent", AnimationComponent.class);
        componentTable.put("ChaseCameraComponent", ChaseCameraComponent.class);
        componentTable.put("ChaseComponent", ChaseComponent.class);
        componentTable.put("DebugBoundsComponent", DebugBoundsComponent.class);
        componentTable.put("GravityComponent", GravityComponent.class);
        componentTable.put("MovementComponent", MovementComponent.class);
        componentTable.put("SpatialComponent", SpatialComponent.class);
        componentTable.put("SpeedComponent", SpeedComponent.class);
        componentTable.put("TransformComponent", TransformComponent.class);
        componentTable.put("CreatorComponent", CreatorComponent.class);
        componentTable.put("WalkComponent", WalkComponent.class);
        componentTable.put("ActorAnimationComponent", ActorAnimationComponent.class);
        componentTable.put("ActionComponent", ActionComponent.class);
        componentTable.put("CameraComponent", CameraComponent.class);
        componentTable.put("StatComponent", StatComponent.class);
        componentTable.put("OwnerComponent", OwnerComponent.class);
        componentTable.put("HeadComponent", HeadComponent.class);
        componentTable.put("AIComponent", AIComponent.class);
        componentTable.put("DataComponent", DataComponent.class);
        componentTable.put("TargetableComponent", TargetableComponent.class);
        componentTable.put("ColliderHistoryComponent", ColliderHistoryComponent.class);
        componentTable.put("TimedLifeComponent", TimedLifeComponent.class);
        componentTable.put("GridColliderComponent", GridColliderComponent.class);
        componentTable.put("PulseCollisionComponent", PulseCollisionComponent.class);
        componentTable.put("ExperienceComponent", ExperienceComponent.class);
        componentTable.put("FilterFlagComponent", FilterFlagComponent.class);
        componentTable.put("BulletComponent", BulletComponent.class);
        componentTable.put("InventoryComponent", InventoryComponent.class);

        // These components should not be created manually.
        componentTable.put("MoveCommandComponent", MovementComponent.class);
        componentTable.put("AbilityCommandComponent", AbilityCommandComponent.class);
        componentTable.put("ModelComponent", ModelComponent.class);
        componentTable.put("BuffedStatComponent", BuffedStatComponent.class);
        componentTable.put("TargetComponent", TargetComponent.class);
        componentTable.put("PhysicsStateComponent", PhysicsStateComponent.class);
        componentTable.put("ItemComponent", ItemComponent.class);

        return componentTable;
    }

    /**
     * Set the bounds on a component given the spatial.
     * @param spatial the spatial to get the bounds from.
     * @param aabbComponent the component to set the bounds of.
     */
    private void setBounds(Spatial spatial, AABBComponent aabbComponent) {
        if (spatial instanceof Node) {
            Spatial boundsData = ((Node) spatial).getChild(Data.getInstance().getModelData().dataNodes.CollisionBounds.Name);
            // If no custom bounds are found use the model bounds depending on the shape.
            if (boundsData != null) {
                Vector3f modelScale = spatial.getLocalScale();
                aabbComponent.localExtentX = (Float) boundsData.getUserData(Data.getInstance().getModelData().dataNodes.CollisionBounds.LengthX) / 2
                        * modelScale.x;
                aabbComponent.localExtentY = (Float) boundsData.getUserData(Data.getInstance().getModelData().dataNodes.CollisionBounds.LengthY) / 2
                        * modelScale.y;
                aabbComponent.localExtentZ = (Float) boundsData.getUserData(Data.getInstance().getModelData().dataNodes.CollisionBounds.LengthZ) / 2
                        * modelScale.z;
                aabbComponent.localOffsetX = boundsData.getLocalTranslation().x * modelScale.x;
                aabbComponent.localOffsetY = boundsData.getLocalTranslation().y * modelScale.y;
                aabbComponent.localOffsetZ = boundsData.getLocalTranslation().z * modelScale.z;
            } else {
                // Find the shape the model uses else defaults to a 2 unit square.
                BoundingVolume bounds = spatial.getWorldBound();
                if (bounds instanceof BoundingBox) {
                    BoundingBox box = (BoundingBox) bounds;
                    aabbComponent.localExtentX = box.getXExtent();
                    aabbComponent.localExtentY = box.getYExtent();
                    aabbComponent.localExtentZ = box.getZExtent();
                } else if (bounds instanceof BoundingSphere) {
                    float radius = ((BoundingSphere) bounds).getRadius();
                    aabbComponent.localExtentX = radius;
                    aabbComponent.localExtentY = radius;
                    aabbComponent.localExtentZ = radius;
                } else {
                    aabbComponent.localExtentX = 1;
                    aabbComponent.localExtentY = 1;
                    aabbComponent.localExtentZ = 1;
                }
            }
        }
    }

    /**
     * Set the camera properties of the entity.
     * @param entity entity the entity to set the camera properties of.
     * @param spatial the spatial to get the camera offset from if available.
     */
    private void setCameraProperties(Entity entity, Node spatial) {
        CameraComponent cameraComponent = entitySystem.getComponent(entity, CameraComponent.class);
        // Create a camera component if none exists and load from the model data if available.
        if (cameraComponent == null) {
            cameraComponent = new CameraComponent();
            Spatial firstPersonNode = spatial.getChild(Data.getInstance().getModelData().dataNodes.cameraNode);
            Vector3f offset = new Vector3f();
            if (firstPersonNode != null) {
                offset.set(firstPersonNode.getLocalTranslation()).multLocal(spatial.getLocalScale());
                TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
                if (transformComponent != null) {
                    offset.multLocal(transformComponent.scaleX, transformComponent.scaleY, transformComponent.scaleZ);
                }
            } else {
                offset.set(0, 0, 0);
            }
            cameraComponent.cameraOffsetX = offset.x;
            cameraComponent.cameraOffsetY = offset.y;
            cameraComponent.cameraOffsetZ = offset.z;
            Float maxCameraDistance = (Float) spatial.getUserData(Data.getInstance().getModelData().properties.MaxCameraDistance);
            if (maxCameraDistance != null) {
                cameraComponent.maxCameraDistance = maxCameraDistance * spatial.getLocalScale().z;
            }
            entitySystem.setComponent(entity, cameraComponent);
        }
    }

    /**
     * Creates an entity without any components.
     * @param name the name of the entity.
     * @return the created entity.
     */
    public final Entity createEntity(String name) {
        return entitySystem.createEntity(name);
    }

    /**
     * Creates an entity without any components.
     * @return the created entity.
     */
    public final Entity createEntity() {
        return entitySystem.createEntity();
    }

    /**
     * Generates components given the name of the actor and the entity.
     * @param name the name of the actor to retrieve components from.
     * @param entity the entity to generate components for.
     */
    private void generateComponents(String name, Entity entity) {
        // Go through all the components in the entity and create it if it exists.
        List<Component> components = actors.get(name);
        for (Component component : components) {
            entitySystem.setComponent(entity, component.copy());
        }
    }

    /**
     * Creates an entity with components that make up an actor.
     * @param dataName the name of the actor data to use for creation.
     * @param player the id of the player that owns the actor.
     * @param entityName the name of the entity.
     * @return the created actor.
     */
    public final Entity createActor(String dataName, int player, String entityName) {
        Entity entity;
        if (entityName == null) {
            entity = entitySystem.createEntity();
        } else {
            entity = entitySystem.createEntity(entityName);
        }
        generateComponents(dataName, entity);

        // Set up components that depend on the spatial model data.
        SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
        if (spatialComponent != null) {
            spatialSystem.addEntity(entity);
            Spatial spatial = spatialSystem.getSpatial(entity);

            // Set the AABB bounds.
            AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
            boolean isManuallySet = aabbComponent != null && (aabbComponent.localExtentX != 0 || aabbComponent.localExtentY != 0 || aabbComponent.localExtentZ != 0
                    || aabbComponent.localOffsetX != 0 || aabbComponent.localOffsetY != 0 || aabbComponent.localOffsetZ != 0);
            if (!isManuallySet) {
                aabbComponent = new AABBComponent();
                setBounds(spatial, aabbComponent);
            }
            entitySystem.setComponent(entity, aabbComponent);

            if (spatial instanceof Node) {
                setCameraProperties(entity, (Node) spatial);
            }
        }

        // All actors must have an actor component.
        ActorComponent actorComponent = entitySystem.getComponent(entity, ActorComponent.class);
        if (actorComponent == null) {
            actorComponent = new ActorComponent();
        }
        actorComponent.type = dataName;
        entitySystem.setComponent(entity, actorComponent);

        // All actors must have an owning player.
        OwnerComponent ownerComponent = entitySystem.getComponent(entity, OwnerComponent.class);
        if (ownerComponent == null) {
            ownerComponent = new OwnerComponent();
        }
        ownerComponent.playerID = player;
        entitySystem.setComponent(entity, ownerComponent);

        if (callbacks != null) {
            callbacks.onActorCreated(entity);
        }

        return entity;
    }

    /**
     * Creates an entity with components that make up an actor.
     * @param dataName the name of the actor data to use for creation.
     * @param player the id of the player that owns the actor.
     * @return the created actor.
     */
    public final Entity createActor(String dataName, int player) {
        return createActor(dataName, player, null);
    }

    /**
     * Change the model of the actor and update the bounds to match.
     * If the entity does not contain a spatial then nothing will happen.
     * @param entity the entity to change the model of.
     * @param model the path of the model.
     */
    public void changeActorModel(Entity entity, String model) {
        // Set up the spatial.
        SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
        if (spatialComponent == null) {
            return;
        }
        spatialComponent.model = model;
        entitySystem.setComponent(entity, spatialComponent);
        spatialSystem.changeModel(entity, model);
        Spatial spatial = spatialSystem.getSpatial(entity);

        // Use the model component to hold original model data.
        ModelComponent modelComponent = new ModelComponent();
        modelComponent.scaleX = spatial.getLocalScale().x;
        modelComponent.scaleY = spatial.getLocalScale().y;
        modelComponent.scaleZ = spatial.getLocalScale().z;
        entitySystem.setComponent(entity, modelComponent);

        // Set the AABB bounds.
        AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
        if (aabbComponent != null) {
            // Do not use model bounds if the bounds are set manually.
            boolean isManuallySet = aabbComponent != null && (aabbComponent.localExtentX != 0 || aabbComponent.localExtentY != 0 || aabbComponent.localExtentZ != 0
                    || aabbComponent.localOffsetX != 0 || aabbComponent.localOffsetY != 0 || aabbComponent.localOffsetZ != 0);
            if (!isManuallySet) {
                aabbComponent = new AABBComponent();
                setBounds(spatial, aabbComponent);
            }
            entitySystem.setComponent(entity, aabbComponent);
        }

        // Set the camera properties of the entity.
        if (spatial instanceof Node) {
            setCameraProperties(entity, (Node) spatial);
        }
    }

    /**
     * @param callbacks the callbacks to set for the factory.
     */
    public void setCallbacks(ActorCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * Adapter for components.
     */
    private class ComponentAdapter extends TypeAdapter<Component> {
        @Override
        public void write(JsonWriter out, Component value) throws IOException {
        }

        @Override
        public Component read(JsonReader in) throws IOException {
            return null;
        }
    }
}
