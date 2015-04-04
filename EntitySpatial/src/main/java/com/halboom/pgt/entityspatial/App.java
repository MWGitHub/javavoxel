package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/22/13
 * Time: 10:08 AM
 */
public class App extends SimpleApplication {
    /**
     * Starts the application.
     * @param args the arguments to pass in.
     */
    public static void main(String[] args) {
        App app = new App();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.getStateManager().attach(new TestState());
    }

    private class TestState extends AbstractAppState {
        /**
         * Simple application.
         */
        private SimpleApplication simpleApp;

        /**
         * Entity system to use.
         */
        private EntitySystem entitySystem = new EntitySystem();

        /**
         * System for managing spatials.
         */
        private SpatialSystem spatialSystem;

        /**
         * Timer for ninja removal.
         */
        private float removalTimer = 0;

        /**
         * Time to remove the ninja.
         */
        private float removeTime = 5f;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            simpleApp = (SimpleApplication) app;
            simpleApp.getFlyByCamera().setMoveSpeed(5);
            simpleApp.getCamera().setLocation(new Vector3f(0, 0, 5f));

            assetManager.registerLocator("assets", FileLocator.class);

            // Create a light so models can be seen.
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            rootNode.addLight(sun);

            AmbientLight globalLight = new AmbientLight();
            globalLight.setColor(ColorRGBA.White.mult(0.3f));
            rootNode.addLight(globalLight);

            // Create the spatial system.
            spatialSystem = new SpatialSystem(entitySystem, assetManager, rootNode);

            // Create walls.
            createWalls(new Vector3f(20f, 20f, 20f));

            // Create a ninja.
            Entity ninja = entitySystem.createEntity("Ninja");
            SpatialComponent spatialComponent = new SpatialComponent();
            spatialComponent.model = "Core/Models/Ninja/Ninja.mesh.j3o";
            entitySystem.setComponent(ninja, spatialComponent);
            TransformComponent transformComponent = new TransformComponent();
            transformComponent.scaleX = 0.01f;
            transformComponent.scaleY = 0.01f;
            transformComponent.scaleZ = 0.01f;
            entitySystem.setComponent(ninja, transformComponent);
            AnimationComponent animationComponent = new AnimationComponent();
            AnimationComponent.AnimationElement element = new AnimationComponent.AnimationElement();
            element.playAnimation = "Walk";
            animationComponent.animations.add(element);
            entitySystem.setComponent(ninja, animationComponent);

            // Create a small bulky upside down ninja.
            ninja = entitySystem.createEntity("Small Ninja");
            spatialComponent = new SpatialComponent();
            spatialComponent.model = "Core/Models/Ninja/Ninja.mesh.j3o";
            entitySystem.setComponent(ninja, spatialComponent);
            transformComponent = new TransformComponent();
            transformComponent.positionX = -2f;
            transformComponent.scaleX = 0.01f;
            transformComponent.scaleY = 0.005f;
            transformComponent.scaleZ = 0.015f;
            Quaternion rotation = new Quaternion(new float[]{0, 180 * FastMath.DEG_TO_RAD, 0});
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationY = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();
            entitySystem.setComponent(ninja, transformComponent);

            // Create a ninja that gets removed.
            Entity removedNinja = entitySystem.createEntity("Removed Ninja");
            spatialComponent = new SpatialComponent();
            spatialComponent.model = "Core/Models/Ninja/Ninja.mesh.j3o";
            entitySystem.setComponent(removedNinja, spatialComponent);
            transformComponent = new TransformComponent();
            transformComponent.positionX = 2f;
            transformComponent.scaleX = 0.01f;
            transformComponent.scaleY = 0.01f;
            transformComponent.scaleZ = 0.01f;
            entitySystem.setComponent(removedNinja, transformComponent);

            // Create a child ninja that also gets removed.
            Entity childNinja = entitySystem.createEntity("Child Ninja");
            spatialComponent = new SpatialComponent();
            spatialComponent.parent = removedNinja;
            spatialComponent.model = "Core/Models/Ninja/Ninja.mesh.j3o";
            entitySystem.setComponent(childNinja, spatialComponent);
            transformComponent = new TransformComponent();
            transformComponent.positionX = 100f;
            transformComponent.scaleX = 0.5f;
            transformComponent.scaleY = 0.5f;
            transformComponent.scaleZ = 0.5f;
            entitySystem.setComponent(childNinja, transformComponent);

            // Create a child ninja that also gets removed.
            ninja = entitySystem.createEntity("Child Ninja");
            spatialComponent = new SpatialComponent();
            spatialComponent.parent = childNinja;
            spatialComponent.model = "Core/Models/Ninja/Ninja.mesh.j3o";
            entitySystem.setComponent(ninja, spatialComponent);
            transformComponent = new TransformComponent();
            transformComponent.positionX = 100f;
            transformComponent.scaleX = 0.5f;
            transformComponent.scaleY = 0.5f;
            transformComponent.scaleZ = 0.5f;
            entitySystem.setComponent(ninja, transformComponent);
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

            removalTimer += tpf;
            if (removalTimer > removeTime) {
                entitySystem.removeEntity(entitySystem.getEntity("Removed Ninja"));
            }

            TransformComponent transformComponent = entitySystem.getComponent(
                    entitySystem.getEntity("Small Ninja"), TransformComponent.class);
            Quaternion rotation = new Quaternion();
            rotation.lookAt(simpleApp.getCamera().getLocation(), Vector3f.UNIT_Y);
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationX = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();

            spatialSystem.update(tpf);

            entitySystem.flushSetChanges();
        }

        /**
         * Creates walls.
         * @param dimensions the dimensions of the walls.
         */
        private void createWalls(Vector3f dimensions) {
            // Mesh to use for the walls
            final float halfTile = 0.5f;
            Box wallBox = new Box(halfTile, halfTile * dimensions.y, dimensions.z * halfTile);
            // Scale the floor texture
            wallBox.scaleTextureCoordinates(new Vector2f(dimensions.x, dimensions.z));
            // Material to use for the walls
            Material wallMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            Texture wallGrid = assetManager.loadTexture("Core/Textures/Grid.png");
            wallMaterial.setTexture("DiffuseMap", wallGrid);
            wallMaterial.setBoolean("UseMaterialColors", true);
            wallMaterial.setColor("Ambient", ColorRGBA.White);
            wallMaterial.setColor("Diffuse", ColorRGBA.White);
            wallMaterial.setColor("Specular", ColorRGBA.Gray);
            wallMaterial.setFloat("Shininess", 2.5f);
            wallMaterial.setReceivesShadows(true);
            wallGrid.setWrap(Texture.WrapMode.Repeat);

            // Create the west wall.
            Entity wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            TransformComponent transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            Geometry wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = -dimensions.x / 2;
            transformComponent.positionY = 0;
            transformComponent.positionZ = 0;

            // Create the east wall.
            wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = dimensions.x / 2;
            transformComponent.positionY = 0;
            transformComponent.positionZ = 0;

            // Create the back wall.
            final float rotationAmount = FastMath.DEG_TO_RAD * 90f;
            wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = 0;
            transformComponent.positionY = 0;
            transformComponent.positionZ = dimensions.z / 2;
            Quaternion rotation = new Quaternion(new float[]{0, rotationAmount, 0});
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationY = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();

            // Create the front wall.
            wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = 0;
            transformComponent.positionY = 0;
            transformComponent.positionZ = -dimensions.z / 2;
            rotation = new Quaternion(new float[]{0, rotationAmount, 0});
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationY = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();

            // Create the top wall.
            wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = 0;
            transformComponent.positionY = dimensions.y / 2;
            transformComponent.positionZ = 0;
            rotation = new Quaternion(new float[]{0, 0, rotationAmount});
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationY = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();

            // Create the bottom wall.
            wallEntity = entitySystem.createEntity("Wall");
            entitySystem.setComponent(wallEntity, new SpatialComponent());
            transformComponent = new TransformComponent();
            entitySystem.setComponent(wallEntity, transformComponent);
            wall = new Geometry("WallSide", wallBox);
            wall.setMaterial(wallMaterial);
            spatialSystem.setSpatial(wallEntity, wall);
            transformComponent.positionX = 0;
            transformComponent.positionY = -dimensions.y / 2;
            transformComponent.positionZ = 0;
            rotation = new Quaternion(new float[]{0, 0, rotationAmount});
            transformComponent.rotationX = rotation.getX();
            transformComponent.rotationY = rotation.getY();
            transformComponent.rotationZ = rotation.getZ();
            transformComponent.rotationW = rotation.getW();
        }
    }
}
