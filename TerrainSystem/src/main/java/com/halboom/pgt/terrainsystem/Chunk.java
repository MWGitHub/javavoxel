package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
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
 * Date: 1/7/13
 * Time: 2:14 PM
 * A chunk merged and optimized that can be separate for editing.
 */
public class Chunk {
    /**
     * Center point of the chunk in world coordinates.
     */
    private Vector3f center;

    /**
     * Starting locations for the chunk.
     */
    private Vector3Int startIndex = new Vector3Int();
    /**
     * Ending locations for the chunk.
     */
    private Vector3Int endIndex = new Vector3Int();

    /**
     * Batched geometries used for the tiles.
     */
    private Spatial batchedGeometry = null;

    /**
     * Mesh optimization to use.
     */
    private TileMesher mesh;

    /**
     * True if the geometry is merged, false otherwise.
     */
    private boolean isMerged = false;

    /**
     * Initializes the chunk and maps the tiles without copying them.
     * @param startIndex the starting index for the chunk.
     * @param endIndex the end index for the chunk.
     * @param scale the scale of the tiles.
     */
    public Chunk(Vector3Int startIndex, Vector3Int endIndex, float scale) {
        mapTiles(startIndex, endIndex);
        center = new Vector3f(
                startIndex.x * scale + (endIndex.x - startIndex.x) * scale / 2,
                startIndex.y * scale + (endIndex.y - startIndex.y) * scale / 2,
                startIndex.z * scale + (endIndex.z - startIndex.z) * scale / 2);

        mesh = new QuadMesh();
    }

    /**
     * Maps a certain section of tiles to the chunk including the end position.
     * Does not copy the tiles into the chunk.
     * @param startIndex the starting index for the chunk.
     * @param endIndex the end index for the chunk.
     */
    public final void mapTiles(Vector3Int startIndex, Vector3Int endIndex) {
        this.startIndex.x = startIndex.x;
        this.startIndex.y = startIndex.y;
        this.startIndex.z = startIndex.z;
        this.endIndex.x = endIndex.x;
        this.endIndex.y = endIndex.y;
        this.endIndex.z = endIndex.z;
    }

    /**
     * Gets an attachable node representing the chunk geometry.
     * @param tiles the tiles to generate the mesh from.
     * @param scale the scale of a tile.
     * @param atlas the tile atlas to use.
     * @param tileBank the data to use for the tiles.
     * @return the attachable node.
     */
    public Spatial createAttachable(byte[][][] tiles, float scale, TileAtlas atlas, TileBank tileBank) {
        if (!isMerged) {
            batchedGeometry = mesh.merge(tiles, scale, atlas, tileBank, startIndex, endIndex);
            // TODO: Set shadow mode elsewhere.
            batchedGeometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            isMerged = true;
        }

        return batchedGeometry;
    }

    /**
     * Attaches the chunk to the given node only if already merged.
     * @param node the node to attach the chunk to.
     */
    public void attach(Node node) {
        if (isMerged && batchedGeometry.getParent() == null) {
            node.attachChild(batchedGeometry);
        }
    }

    /**
     * Detaches the chunk from the attached node and frees memory.
     */
    public void detach() {
        if (batchedGeometry != null) {
            mesh.destroy(batchedGeometry);
            batchedGeometry.removeFromParent();

            if (batchedGeometry instanceof Geometry) {
                Geometry geom = (Geometry) batchedGeometry;
                Mesh geomMesh = geom.getMesh();
                List<VertexBuffer> buffers = geomMesh.getBufferList();
                for (VertexBuffer buffer : buffers) {
                    BufferUtils.destroyDirectBuffer(buffer.getData());
                }
            }

            batchedGeometry = null;
            isMerged = false;
        }
    }

    /**
     * Sets the chunk as dirty so that it will use new information on the next update.
     */
    public void setDirty() {
        detach();
        mesh.setDirty();
    }

    /**
     * Updates the tiles by optimizing and re-attaching them if needed.
     * @param tiles the tiles to generate the mesh from.
     * @param scale the scale of a tile.
     * @param atlas the tile atlas to use.
     * @param tileBank the data to use for the tiles.
     * @param node the node to attach to.
     */
    public void updateTiles(byte[][][] tiles, float scale, TileAtlas atlas, TileBank tileBank, Node node) {
        detach();
        mesh.setDirty();
        createAttachable(tiles, scale, atlas, tileBank);
        attach(node);
    }

    /**
     * @return the center of the chunk in world units.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * @return true if merged.
     */
    public boolean getIsMerged() {
        return isMerged;
    }

    /**
     * @return the batched geometry.
     */
    public Spatial getBatchedGeometry() {
        return batchedGeometry;
    }

    /**
     * Destroys the chunks.
     */
    public void destroy() {
        detach();
    }
}
