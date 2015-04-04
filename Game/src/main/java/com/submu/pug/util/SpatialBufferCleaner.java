package com.submu.pug.util;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/2/13
 * Time: 11:58 AM
 * Clears the buffers in spatials.
 */
public final class SpatialBufferCleaner {
    /**
     * Disallow instantiation.
     */
    private SpatialBufferCleaner() {

    }

    /**
     * Destroys the buffers used in spatials.
     * @param spatial the spatial to clear.
     */
    public static void destroyBuffers(Spatial spatial) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                destroyBuffers(child);
            }
        }
        // Clear the root geometry if it has any.
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            Mesh geomMesh = geom.getMesh();
            List<VertexBuffer> buffers = geomMesh.getBufferList();
            for (VertexBuffer buffer : buffers) {
                BufferUtils.destroyDirectBuffer(buffer.getData());
            }
        }
    }
}
