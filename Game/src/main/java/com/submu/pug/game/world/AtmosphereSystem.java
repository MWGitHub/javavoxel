package com.submu.pug.game.world;

import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/2/13
 * Time: 2:10 PM
 * Creates, removes, and updates objects.
 */
public class AtmosphereSystem implements Subsystem {
    /**
     * Asset manager to use to load assets from.
     */
    private AssetManager assetManager;

    /**
     * Entity system for the game.
     */
    private EntitySystem entitySystem;

    /**
     * Root of the world.
     */
    private Node worldRoot = new Node();

    /**
     * Objects of the world excluding the terrain.
     */
    private Node objects = new Node();

    /**
     * Skybox of the world.
     */
    private Spatial skybox;

    /**
     * Light that represents the sun.
     */
    private DirectionalLight sun = new DirectionalLight();

    /**
     * Ambient light for all areas.
     */
    private AmbientLight ambientLight = new AmbientLight();

    /**
     * Callbacks for the world.
     */
    private AtmosphereCallbacks callbacks;

    /**
     * Initializes the terrain and attaches it to the rootNode of the given app.
     * @param assetManager the assetManager to load from.
     * @param entitySystem the entity system of the game.
     * @param root the node to attach to.
     */
    public AtmosphereSystem(AssetManager assetManager, EntitySystem entitySystem, Node root) {
        this.assetManager = assetManager;
        this.entitySystem = entitySystem;

        // Create the object holder.
        root.attachChild(worldRoot);
        worldRoot.attachChild(objects);

        // Add lights to the root to make sure the tiles are also lit.
        sun.setColor(ColorRGBA.White);
        sun.setDirection(Vector3f.ZERO);
        root.addLight(sun);

        // Create the ambient light.
        ambientLight.setColor(ColorRGBA.White);
        root.addLight(ambientLight);
    }

    /**
     * Create clouds for the terrain.
     * @param xLength the amount of cloud tiles in the x direction.
     * @param zLength the amount of cloud tiles in the z direction.
     * @param scale the scale of the cloud.
     */
    public void createClouds(int xLength, int zLength, float scale) {
        for (int x = 0; x < xLength; x++) {
            for (int z = 0; z < zLength; z++) {
                Node cloud = (Node) assetManager.loadModel("Core/Models/Cloud/Cloud.j3o");
                Geometry geometry = (Geometry) cloud.getChild("Cloud Geometry");
                //geometry.getMaterial().setColor("Color", ColorRGBA.White);
                cloud.setLocalScale(scale, 1f, scale);
                BoundingBox bounds = (BoundingBox) cloud.getWorldBound();
                cloud.setLocalTranslation(
                        -xLength / 2 * bounds.getXExtent() + bounds.getXExtent() * 2 * x,
                        -bounds.getYExtent(),
                        -zLength / 2 * bounds.getXExtent() + bounds.getZExtent() * 2 * z);
                worldRoot.attachChild(cloud);
            }
        }
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * @param color the color of the sun to set.
     */
    public void setSunColor(ColorRGBA color) {
        sun.setColor(color);
    }

    /**
     * @param direction the direction of the sun.
     */
    public void setSunDirection(Vector3f direction) {
        sun.setDirection(direction);
    }

    /**
     * @return the sun light.
     */
    public DirectionalLight getSun() {
        return sun;
    }

    /**
     * @param color the color to set the ambient light.
     */
    public void setAmbientLightColor(ColorRGBA color) {
        ambientLight.setColor(color);
    }

    /**
     * @return the ambient light.
     */
    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    /**
     * Set the skybox to the given textures.
     * @param west the west texture.
     * @param east the east texture.
     * @param north the north texture.
     * @param south the south texture.
     * @param up the up texture.
     * @param down the down texture.
     */
    public void setSkybox(String west, String east, String north, String south, String up, String down) {
        if (skybox != null) {
            skybox.removeFromParent();
        }
        skybox = SkyFactory.createSky(assetManager,
                assetManager.loadTexture(west), assetManager.loadTexture(east),
                assetManager.loadTexture(north), assetManager.loadTexture(south),
                assetManager.loadTexture(up), assetManager.loadTexture(down));
        worldRoot.attachChild(skybox);
        if (callbacks != null) {
            callbacks.onSkyboxChanged(skybox);
        }
    }

    /**
     * @param callbacks the callback function to set for the world.
     */
    public void setCallbacks(AtmosphereCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void destroy() {

    }
}
