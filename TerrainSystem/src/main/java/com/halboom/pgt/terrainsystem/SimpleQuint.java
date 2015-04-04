package com.halboom.pgt.terrainsystem;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/29/13
 * Time: 3:14 PM
 * Represents a five vertex surface.
 * The simplified quint uses the same normals as a quad.
 */
public class SimpleQuint extends Mesh {
    /**
     * Vertices per quint.
     */
    public static final int VERTICES = 5;

    /**
     * Triangles per quint.
     */
    public static final int TRIANGLES = 4;

    /**
     * Width of the quint.
     */
    private float width;

    /**
     * Height of the quint.
     */
    private float height;

    /**
     * Midpoints for the middle vertex.
     */
    private Vector3f midpoint = new Vector3f();

    /**
     * Set to true to skip setting buffers that do not change.
     */
    private boolean isSetUp = false;

    /**
     * Texture locations.
     */
    private float texStartX = 0, texEndX = 1, texStartY = 0, texEndY = 1;

    /**
     * Serialization only. Do not use.
     */
    public SimpleQuint() {
    }

    /**
     * Create a quint with the given width and height.
     * The quint has a middle vertex can be moved around.
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param midpoint the position of the middle vertex.
     */
    public SimpleQuint(float width, float height, Vector3f midpoint) {
        this.width = width;
        this.height = height;
        this.midpoint.set(midpoint);
        updateGeometry();
        isSetUp = true;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    /**
     * @return the midpoint of the quint.
     */
    public Vector3f getMidpoint() {
        return midpoint;
    }

    /**
     * Sets the midpoint and updates the mesh.
     * @param midpoint the midpoint to set.
     */
    public void setMidpoint(Vector3f midpoint) {
        this.midpoint.set(midpoint);
        updateGeometry();
    }

    /**
     * Set the textures of the quint given the texture coordinates of a square.
     * @param startX the starting X position of the texture.
     * @param endX the ending X position of the texture.
     * @param startY the starting Y position of the texture.
     * @param endY the ending Y position of the texture.
     */
    public void setTexture(float startX, float endX, float startY, float endY) {
        texStartX = startX;
        texEndX = endX;
        texStartY = startY;
        texEndY = endY;
        updateGeometry();
    }

    /**
     * Resets the quint.
     */
    public void reset() {
        midpoint.x = width / 2;
        midpoint.y = height / 2;
        midpoint.z = 0;
        updateGeometry();
    }

    /**
     * Updates the geometry of the quint.
     */
    public void updateGeometry() {
        final int indexPerVertex = 3;
        final int indexPerTexture = 2;
        /**
         * 3------2
         * |\    /|
         * | \  / |
         * |  44  |
         * | /  \ |
         * |/    \|
         * 0------1
         */
        setBuffer(VertexBuffer.Type.Position, indexPerVertex, new float[]{
                0,      0,      0,
                width,  0,      0,
                width,  height, 0,
                0,      height, 0,
                midpoint.x,  midpoint.y,  midpoint.z
        });
        float normX = midpoint.x / width * (texEndX - texStartX) + texStartX;
        float normY = midpoint.y / height * (texEndY - texStartY) + texStartY;
        setBuffer(VertexBuffer.Type.TexCoord, indexPerTexture, new float[]{
                texStartX, texStartY,
                texEndX, texStartY,
                texEndX, texEndY,
                texStartX, texEndY,
                normX, normY,
        });
        setBuffer(VertexBuffer.Type.Normal, indexPerVertex, new float[]{
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1
        });
        if (!isSetUp) {
            if (height < 0) {
                setBuffer(VertexBuffer.Type.Index, indexPerVertex, new short[]{
                        0, 4, 1,
                        0, 3, 4,
                        1, 4, 2,
                        4, 3, 2
                });
            } else {
                setBuffer(VertexBuffer.Type.Index, indexPerVertex, new short[]{
                        0, 1, 4,
                        3, 0, 4,
                        1, 2, 4,
                        2, 3, 4,
                });
            }
        }

        updateBound();
    }
}
