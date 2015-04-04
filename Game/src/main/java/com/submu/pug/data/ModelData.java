package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/8/13
 * Time: 2:54 PM
 * Data that is common to all models in the game.
 * Mainly holds names of spatials and properties.
 */
public class ModelData {
    /**
     * Nodes used as data points with location.
     */
    public DataNode dataNodes;

    /**
     * Properties for the model.
     */
    public Properties properties;

    /**
     * Nodes attached to the model matching the name.
     */
    public static class DataNode {
        /**
         * Name of the first person node.
         */
        public String cameraNode;

        /**
         * Name of the root attachments node.
         */
        public String attachRootNode;

        /**
         * Node names within the attached node.
         */
        public AttachedNodes attachedNodes;

        /**
         * Collision cylinder used for model to grid collision detection.
         */
        public CollisionBounds CollisionBounds;

        /**
         * Properties of the collision cylinder.
         */
        public static class CollisionBounds {
            public String Name;
            public String LengthX;
            public String LengthY;
            public String LengthZ;
        }

        /**
         * Node names that can be in the attached nodes.
         */
        public static class AttachedNodes {
            /**
             * Location to attach heads to.
             */
            public String head;

            /**
             * Base location of the model to use when attaching to objects.
             */
            public String base;
        }
    }

    /**
     * Properties of the base model.
     */
    public static class Properties {
        // Default maximum camera distance for the model, the user can override in the editor.
        // TODO: Move this to the object data.
        public String MaxCameraDistance;
    }
}
