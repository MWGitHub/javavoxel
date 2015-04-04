package com.halboom.pgt.terrainsystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/29/13
 * Time: 3:14 PM
 * Represents a five (actually more because of normals) vertex surface.
 */
public class Quint extends Mesh {
    /**
     * Vertices per quint.
     */
    public static final int VERTICES = 12;

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
     * True to align textures.
     */
    private boolean isTextureAligned = false;

    /**
     * Serialization only. Do not use.
     */
    public Quint() {
    }

    /**
     * Create a quint with the given width and height.
     * The quint has a middle vertex can be moved around.
     * @param width the X extent or width
     * @param height the Y extent or width
     * @param midpoint the position of the middle vertex.
     */
    public Quint(float width, float height, Vector3f midpoint) {
        this.width = width;
        this.height = height;
        this.midpoint.set(midpoint);
        this.isTextureAligned = isTextureAligned;
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
     * @param textureAligned true to align the textures when recalculating.
     */
    public void setTextureAligned(boolean textureAligned) {
        isTextureAligned = textureAligned;
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
         * Used when vertices are shared.
         * 3------2
         * |\    /|
         * | \  / |
         * |  44  |
         * | /  \ |
         * |/    \|
         * 0------1
         */
        setBuffer(VertexBuffer.Type.Position, indexPerVertex, new float[]{
                // Bottom triangle
                0, 0, 0,
                width, 0, 0,
                midpoint.x,  midpoint.y,  midpoint.z,
                // Right triangle
                width, 0, 0,
                width, height, 0,
                midpoint.x,  midpoint.y,  midpoint.z,
                // Top triangle
                width, height, 0,
                0, height, 0,
                midpoint.x,  midpoint.y,  midpoint.z,
                // Left triangle
                0, height, 0,
                0, 0, 0,
                midpoint.x,  midpoint.y,  midpoint.z,
        });
        // Set the texture positions.
        float normX;
        float normY;
        // Align textures perfectly if enabled.
        if (isTextureAligned) {
            normX = midpoint.x / width * (texEndX - texStartX) + texStartX;
            normY = midpoint.y / height * (texEndY - texStartY) + texStartY;
        } else {
            normX = 0.5f / width * (texEndX - texStartX) + texStartX;
            normY = 0.5f / height * (texEndY - texStartY) + texStartY;
        }

        setBuffer(VertexBuffer.Type.TexCoord, indexPerTexture, new float[]{
                texStartX, texStartY,
                texEndX, texStartY,
                normX, normY,
                texEndX, texStartY,
                texEndX, texEndY,
                normX, normY,
                texEndX, texEndY,
                texStartX, texEndY,
                normX, normY,
                texStartX, texEndY,
                texStartX, texStartY,
                normX, normY
        });
        // Set the normals.
        Vector3f n1 = FastMath.computeNormal(new Vector3f(0, 0, 0), new Vector3f(width, 0, 0), midpoint);
        Vector3f n2 = FastMath.computeNormal(new Vector3f(width, 0, 0), new Vector3f(width, height, 0), midpoint);
        Vector3f n3 = FastMath.computeNormal(new Vector3f(width, height, 0), new Vector3f(0, height, 0), midpoint);
        Vector3f n4 = FastMath.computeNormal(new Vector3f(0, height, 0), new Vector3f(0, 0, 0), midpoint);
        setBuffer(VertexBuffer.Type.Normal, indexPerVertex, new float[]{
                n1.x, n1.y, n1.z,
                n1.x, n1.y, n1.z,
                n1.x, n1.y, n1.z,

                n2.x, n2.y, n2.z,
                n2.x, n2.y, n2.z,
                n2.x, n2.y, n2.z,

                n3.x, n3.y, n3.z,
                n3.x, n3.y, n3.z,
                n3.x, n3.y, n3.z,

                n4.x, n4.y, n4.z,
                n4.x, n4.y, n4.z,
                n4.x, n4.y, n4.z
        });
        // Set the indices.
        if (!isSetUp) {
            if (height < 0) {
                setBuffer(VertexBuffer.Type.Index, indexPerVertex, new short[]{
                        2, 1, 0,
                        5, 4, 3,
                        8, 7, 6,
                        11, 10, 9
                });
            } else {
                setBuffer(VertexBuffer.Type.Index, indexPerVertex, new short[]{
                        0, 1, 2,
                        3, 4, 5,
                        6, 7, 8,
                        9, 10, 11,
                });
            }
        }

        updateBound();
    }
}
