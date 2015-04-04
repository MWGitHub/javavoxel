package com.halboom.pgt.physics;

import com.halboom.pgt.entityspatial.SpatialComponent;
import com.halboom.pgt.entityspatial.SpatialSystem;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.pgutil.threading.Threading;
import com.halboom.pgt.physics.bullet.components.BulletComponent;
import com.halboom.pgt.physics.debug.DebugBoundsComponent;
import com.halboom.pgt.physics.debug.DebugGrid;
import com.halboom.pgt.physics.filters.ColliderHistoryComponent;
import com.halboom.pgt.physics.filters.PulseCollisionComponent;
import com.halboom.pgt.physics.simple.CollisionCallbacks;
import com.halboom.pgt.physics.simple.CollisionInformation;
import com.halboom.pgt.physics.simple.CollisionResolver;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.GridColliderComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/9/13
 * Time: 6:02 PM
 * Class to test the manager with.
 */
public final class App extends SimpleApplication {
    /**
     * Starts the test application.
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
        this.getStateManager().attach(new TerrainTest());
    }

    @Override
    public void destroy() {
        super.destroy();
        Threading.getInstance().destroy();
    }

    /**
     * Test state for the terrain.
     */
    private class TerrainTest extends AbstractAppState implements ActionListener {
        /**
         * Entity system.
         */
        private EntitySystem entitySystem;

        /**
         * Collision resolver to use.
         */
        private CollisionResolver collisionResolver;

        /**
         * Subsystems to iterate through.
         */
        private List<Subsystem> systems = new LinkedList<Subsystem>();

        /**
         * Spatial system.
         */
        private SpatialSystem spatialSystem;

        /**
         * System with all the physics set up already.
         */
        private PhysicsSystem physicsSystem;

        /**
         * Movement directions.
         */
        private boolean isLeft = false, isRight = false, isUp = false, isDown = false, isForward = false, isBackward = false;

        /**
         * System for debugging the grid.
         */
        private DebugGrid debugGrid;

        /**
         * Player entity.
         */
        private Entity player;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);

            DirectionalLight sun = new DirectionalLight();
            sun.setColor(ColorRGBA.White);
            sun.setDirection(new Vector3f(1, 1, -1));
            rootNode.addLight(sun);

            AmbientLight light = new AmbientLight();
            light.setColor(ColorRGBA.White);
            rootNode.addLight(light);

            collisionResolver = new CollisionResolver();

            entitySystem = new EntitySystem();

            physicsSystem = new PhysicsSystem(entitySystem);
            physicsSystem.addCollisionCallbacks(new CollisionCallbacks() {
                @Override
                public void onBlockerCollide(CollisionInformation collisionInformation) {
                    //System.out.println("Physics App - COLLIDE");
                }

                @Override
                public void onSensorCollide(CollisionInformation collisionInformation) {
                    Entity collider = collisionInformation.getCollider();
                    Entity collidee = collisionInformation.getCollidee();
                    ColliderHistoryComponent history = entitySystem.getComponent(collider, ColliderHistoryComponent.class);
                    if (history != null) {
                        if (history.newColliders.contains(collidee)) {
                            //System.out.println("Physics App - SENSOR COLLIDE - " + System.nanoTime());
                        }
                    }
                    if (collider.getName() != null && collider.getName().equals("pulse")) {
                        //System.out.println("Physics App - Hit Pulse");
                    }
                }
            });

            // Generate random terrain
            byte[][][] tile = new byte[32][32][32];
            for (int x = 0; x < tile.length; x++) {
                for (int z = 0; z < tile[x][0].length; z++) {
                    int height = (int) (Math.random() * (tile[x].length - 0.01));
                    for (int y = 0; y < height; y++) {
                        tile[x][y][z] = 1;
                    }
                }
            }

            physicsSystem.setTiles(tile);
            physicsSystem.enableDebug(assetManager, rootNode);
            systems.add(physicsSystem);
            spatialSystem = new SpatialSystem(entitySystem, assetManager, rootNode);
            systems.add(spatialSystem);

            Spatial commandCenter = assetManager.loadModel("com/halboom/pgt/physics/Models/Command Center/Command Center.j3o");
            rootNode.attachChild(commandCenter);
            physicsSystem.addStaticObject(commandCenter);

            createObject();
            setupKeys();

            debugGrid = new DebugGrid(assetManager);
            debugGrid.setTiles(tile);
            debugGrid.enable(rootNode);
            physicsSystem.getBulletSystem().enableDebug(stateManager);

            Box box = new Box(5f, 5f, 5f);
            Node testGeom = new Node();
            Geometry boxGeom = new Geometry("Geom", box);
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            boxGeom.setMaterial(material);
            testGeom.attachChild(boxGeom);
            testGeom.setLocalTranslation(-10f, 0, -10f);
            physicsSystem.addStaticObject(testGeom);
            rootNode.attachChild(testGeom);
        }

        /**
         * Set up the keys.
         */
        private void setupKeys() {
            getFlyByCamera().setMoveSpeed(10f);
            inputManager.addMapping("Strafe Left",
                    new KeyTrigger(KeyInput.KEY_A),
                    new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping("Strafe Right",
                    new KeyTrigger(KeyInput.KEY_D),
                    new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping("Walk Forward",
                    new KeyTrigger(KeyInput.KEY_W),
                    new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping("Walk Backward",
                    new KeyTrigger(KeyInput.KEY_S),
                    new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping("Move Up", new KeyTrigger(KeyInput.KEY_Q));
            inputManager.addMapping("Move Down", new KeyTrigger(KeyInput.KEY_Z));
            inputManager.addListener(this, "Strafe Left", "Strafe Right");
            inputManager.addListener(this, "Walk Forward", "Walk Backward");
            inputManager.addListener(this, "Move Up", "Move Down");
        }


        @Override
        public void onAction(String binding, boolean isPressed, float tpf) {
            if (binding.equals("Strafe Left")) {
                isLeft = isPressed;
            } else if (binding.equals("Strafe Right")) {
                isRight = isPressed;
            } else if (binding.equals("Walk Forward")) {
                isForward = isPressed;
            } else if (binding.equals("Walk Backward")) {
                isBackward = isPressed;
            } else if (binding.equals("Move Up")) {
                isUp = isPressed;
            } else if (binding.equals("Move Down")) {
                isDown = isPressed;
            }
        }

        /**
         * Updates the movement direction.
         * @param tpf the time per frame.
         */
        private void updateMoveDirection(float tpf) {
            final float moveSpeed = 50f;
            Entity box = entitySystem.getEntity("Box");
            SpeedComponent speedComponent = entitySystem.getComponent(box, SpeedComponent.class);
            Vector3f camDir = cam.getDirection().mult(moveSpeed * tpf);
            Vector3f camLeft = cam.getLeft().mult(moveSpeed * tpf);
            Vector3f walkDirection = new Vector3f();
            camDir.y = 0;
            camLeft.y = 0;
            if (isLeft) {
                walkDirection.addLocal(camLeft);
            }
            if (isRight) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (isForward) {
                walkDirection.addLocal(camDir);
            }
            if (isBackward) {
                walkDirection.addLocal(camDir.negate());
            }
            if (isUp) {
                speedComponent.accelY = moveSpeed * tpf;
            }
            if (isDown) {
                speedComponent.accelY = -moveSpeed * tpf;
            }
            speedComponent.accelX = walkDirection.x;
            speedComponent.accelZ = walkDirection.z;
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

            // Move the box along with the camera.
            updateMoveDirection(tpf);

            SpeedComponent speedComponent = entitySystem.getComponent(player, SpeedComponent.class);
            for (Subsystem system : systems) {
                system.update(tpf);
            }

            for (Subsystem system : systems) {
                system.cleanupSubsystem();
            }

            entitySystem.flushSetChanges();
        }

        /**
         * Creates an object that is collidable.
         */
        private void createObject() {
            Box box = new Box(0.5f, 1.0f, 0.5f); // create a 1x1x1 box shape at the origin
            Geometry geom = new Geometry("Box", box);  // create a cube geometry from the box shape
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
            geom.setMaterial(mat);                   // set the cube geometry 's material
            ChaseCamera chaseCamera = new ChaseCamera(getCamera(), geom, inputManager);
            getFlyByCamera().setEnabled(false);
            chaseCamera.setEnabled(true);
            chaseCamera.setDragToRotate(false);
            chaseCamera.setInvertVerticalAxis(true);
            chaseCamera.setMaxVerticalRotation(85 * FastMath.DEG_TO_RAD);
            chaseCamera.setMinVerticalRotation(-85 * FastMath.DEG_TO_RAD);

            player = entitySystem.createEntity("Box");
            SpeedComponent speedComponent = new SpeedComponent();
            speedComponent.maxSpeedHorizontal = 5f;
            speedComponent.maxSpeedVertical = 5f;
            speedComponent.isHorizontalDamped = true;
            speedComponent.isVerticalDamped = true;

            speedComponent.damping = 0.99f;
            entitySystem.setComponent(player, speedComponent);
            entitySystem.setComponent(player, new MovementComponent());
            AABBComponent aabbComponent = new AABBComponent();
            aabbComponent.localExtentX = 0.5f;
            aabbComponent.localExtentY = 1.0f;
            aabbComponent.localExtentZ = 0.5f;

            entitySystem.setComponent(player, new GridColliderComponent());

            entitySystem.setComponent(player, aabbComponent);
            entitySystem.setComponent(player, new SpatialComponent());
            spatialSystem.setSpatial(player, geom);
            entitySystem.setComponent(player, new TransformComponent());
            CollisionComponent collisionComponent = new CollisionComponent();
            collisionComponent.targets = 1 | 1 << 1 | 1 << 3;
            collisionComponent.groups = 1 << 3;
            entitySystem.setComponent(player, collisionComponent);
            entitySystem.setComponent(player, new DebugBoundsComponent());
            entitySystem.setComponent(player, new BulletComponent());

            // Create a sensor to check for history.
            Entity sensor = entitySystem.createEntity();
            TransformComponent transformComponent = new TransformComponent();
            transformComponent.positionZ = -5;
            entitySystem.setComponent(sensor, transformComponent);
            aabbComponent = new AABBComponent();
            aabbComponent.localExtentX = 0.5f;
            aabbComponent.localExtentY = 0.5f;
            aabbComponent.localExtentZ = 0.5f;
            entitySystem.setComponent(sensor, aabbComponent);
            collisionComponent = (CollisionComponent) collisionComponent.copy();
            collisionComponent.isSensor = true;
            entitySystem.setComponent(sensor, collisionComponent);
            entitySystem.setComponent(sensor, new DebugBoundsComponent());
            entitySystem.setComponent(sensor, new ColliderHistoryComponent());

            // Create a sensor to check for pulse.
            sensor = entitySystem.createEntity("pulse");
            transformComponent = new TransformComponent();
            transformComponent.positionZ = 5;
            entitySystem.setComponent(sensor, transformComponent);
            aabbComponent = new AABBComponent();
            aabbComponent.localExtentX = 0.5f;
            aabbComponent.localExtentY = 0.5f;
            aabbComponent.localExtentZ = 0.5f;
            entitySystem.setComponent(sensor, aabbComponent);
            collisionComponent = (CollisionComponent) collisionComponent.copy();
            collisionComponent.isSensor = true;
            entitySystem.setComponent(sensor, collisionComponent);
            entitySystem.setComponent(sensor, new DebugBoundsComponent());
            PulseCollisionComponent pulseCollisionComponent = new PulseCollisionComponent();
            pulseCollisionComponent.period = 1;
            entitySystem.setComponent(sensor, pulseCollisionComponent);

            // Create physics objects.
            box = new Box(50, 1, 50);
            geom = new Geometry("Box", box);
            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.randomColor());   // set color of material to blue
            geom.setMaterial(mat);
            Entity entity = entitySystem.createEntity("Floor");
            entitySystem.setComponent(entity, new SpatialComponent());
            spatialSystem.setSpatial(entity, geom);
            transformComponent = new TransformComponent();
            transformComponent.positionY = -5f;
            entitySystem.setComponent(entity, transformComponent);
            collisionComponent = new CollisionComponent();
            collisionComponent.groups = 1 << 1;
            collisionComponent.targets = 1 << 3;
            //collisionComponent.isSensor = true;
            entitySystem.setComponent(entity, collisionComponent);
            aabbComponent = new AABBComponent();
            aabbComponent.localExtentX = 50;
            aabbComponent.localExtentY = 1;
            aabbComponent.localExtentZ = 50;
            entitySystem.setComponent(entity, aabbComponent);
            speedComponent = new SpeedComponent();
            //speedComponent.speedY = 1f;
            speedComponent.isVerticalDamped = false;
            entitySystem.setComponent(entity, speedComponent);
            entitySystem.setComponent(entity, new MovementComponent());
        }
    }
}