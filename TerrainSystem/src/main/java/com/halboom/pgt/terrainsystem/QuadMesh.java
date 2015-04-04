package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.Random;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/18/13
 * Time: 1:05 PM
 * Optimizes by only creating quads at borders without tiles.
 */
public class QuadMesh implements TileMesher {
    /**
     * Facing direction of a quad.
     */
    private enum Direction {
        /**
         * No direction.
         */
        NONE,
        /**
         * Different directions to face.
         */
        WEST, EAST, NORTH, SOUTH, UP, DOWN
    }

    /**
     * Directional sides in byte form for bitwise operations.
     */
    private static final byte SIDE_NONE = 0,
            SIDE_WEST = 1, SIDE_EAST = 2,
            SIDE_NORTH = 4, SIDE_SOUTH = 8,
            SIDE_UP = 16, SIDE_DOWN = 32;

    /**
     * Tile side indices.
     */
    private static final int INDEX_WEST = 0, INDEX_EAST = 1, INDEX_NORTH = 2,
            INDEX_SOUTH = 3, INDEX_UP = 4, INDEX_DOWN = 5;

    /**
     * Tile angles rotations depending on the orientation.
     */
    private static final float ANGLE_WEST = FastMath.DEG_TO_RAD * 270,
    ANGLE_EAST = FastMath.DEG_TO_RAD * 90,
    ANGLE_NORTH = FastMath.DEG_TO_RAD * 180,
    ANGLE_SOUTH = 0,
    ANGLE_UP = FastMath.DEG_TO_RAD * 270,
    ANGLE_DOWN = FastMath.DEG_TO_RAD * 90;

    /**
     * Numbers for the index when incrementing.
     */
    private static final int INDICES_VERTEX = 0, INDICES_TRIANGLE = 1;

    /**
     * True if the mesh has been merged before and merge data has not been deleted.
     */
    private boolean isOptimized = false;

    /**
     * Tiles for use with the editor, increases memory requirements but decreases meshing time.
     */
    private byte[][][] optimizedTiles;

    /**
     * Rotation quaternion used for rotating quads.
     */
    private static Quaternion rotation = new Quaternion();

    /**
     * Number of components per vertex group.
     */
    private static final int COMPONENTS = 3;
    /**
     * Number of components per vertex texture group.
     */
    private static final int COMPONENTS_PER_TEXTURE = 2;
    /**
     * Triangles per type of shape.
     */
    private static final int TRIANGLES_PER_QUAD = 2;
    /**
     * Vertices per type of shape.
     */
    private static final int VERTICES_PER_QUAD = 4;

    /**
     * Set to true to use quints instead of quads.
     * Reduces performance when toggled on.
     */
    private boolean areQuintsUsed = true;
    /**
     * Midpoint used for quints.
     */
    private Vector3f midpoint = new Vector3f();


    /**
     * Stores the most recently used texture position.
     */
    private TileAtlas.TexturePosition texturePosition = new TileAtlas.TexturePosition();

    /**
     * Initializes the mesh.
     */
    public QuadMesh() {
    }

    /**
     * Merges the tiles in a chunk.
     * @param tiles the tiles to load from.
     * @param scale the scale of a tile.
     * @param atlas the atlas to get the material and coordinates from.
     * @param tileBank the data to use for the tiles.
     * @param startIndex the starting index of the chunk.
     * @param endIndex the end index of the chunk.
     * @return the optimized mesh based on the tiles.
     */
    public Spatial merge(byte[][][] tiles, float scale, TileAtlas atlas, TileBank tileBank, Vector3Int startIndex, Vector3Int endIndex) {
        // Only merge tiles if they have not been merged before.
        if (!isOptimized) {
            optimizedTiles = new byte[endIndex.x - startIndex.x + 1][endIndex.y - startIndex.y + 1][endIndex.z - startIndex.z + 1];
            for (int x = startIndex.x; x <= endIndex.x; x++) {
                for (int y = startIndex.y; y <= endIndex.y; y++) {
                    for (int z = startIndex.z; z <= endIndex.z; z++) {
                        if (tiles[x][y][z] != 0 && !isTileSurrounded(tiles, x, y, z)) {
                            byte direction = getTileVisibleDirections(tiles, x, y, z);
                            optimizedTiles[x - startIndex.x][y - startIndex.y][z - startIndex.z] = direction;
                        }
                    }
                }
            }
            isOptimized = true;
        }

        return optimize(tiles, scale, atlas, tileBank, startIndex);
    }

    /**
     * Copy data within a shared mesh with geometry translations.
     * @param inGeom the geometry translations to use on the mesh.
     * @param outMesh the mesh to copy data to.
     * @param indices indices to increment, first index is vertex, second is triangles.
     */
    private void copyMeshData(Geometry inGeom, Mesh outMesh, int[] indices) {
        // Update the geometry positions.
        inGeom.computeWorldMatrix();
        Mesh inMesh = inGeom.getMesh();
        Matrix4f worldMatrix = inGeom.getWorldMatrix();

        int geomVertCount = inMesh.getVertexCount();
        int geomTriCount = inMesh.getTriangleCount();

        // Fill the index data.
        IndexBuffer inIdx = inMesh.getIndicesAsList();
        IndexBuffer outIdx = outMesh.getIndexBuffer();
        for (int tri = 0; tri < geomTriCount; tri++) {
            for (int comp = 0; comp < COMPONENTS; comp++) {
                int idx = inIdx.get(tri * COMPONENTS + comp) + indices[INDICES_VERTEX];
                outIdx.put((indices[INDICES_TRIANGLE] + tri) * COMPONENTS + comp, idx);
            }
        }

        // Fill the position data.
        VertexBuffer inBuf = inMesh.getBuffer(VertexBuffer.Type.Position);
        VertexBuffer outBuf = outMesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer inPos = (FloatBuffer) inBuf.getDataReadOnly();
        FloatBuffer outPos = (FloatBuffer) outBuf.getData();
        doTransformVerts(inPos, indices[INDICES_VERTEX], outPos, worldMatrix);

        // Fill the normal data.
        inBuf = inMesh.getBuffer(VertexBuffer.Type.Normal);
        outBuf = outMesh.getBuffer(VertexBuffer.Type.Normal);
        inPos = (FloatBuffer) inBuf.getDataReadOnly();
        outPos = (FloatBuffer) outBuf.getData();
        doTransformNorms(inPos, indices[INDICES_VERTEX], outPos, worldMatrix);

        // Fill the texture data
        inBuf = inMesh.getBuffer(VertexBuffer.Type.TexCoord);
        outBuf = outMesh.getBuffer(VertexBuffer.Type.TexCoord);
        inBuf.copyElements(0, outBuf, indices[INDICES_VERTEX], geomVertCount);

        indices[INDICES_VERTEX] += geomVertCount;
        indices[INDICES_TRIANGLE] += geomTriCount;
    }

    /**
     * Creates and attaches the tile to a node.
     * @param tiles the tiles to optimize.
     * @param scale the scale of the blocks.
     * @param atlas the atlas to get texture data from.
     * @param tileBank the tile data to use.
     * @param startIndex the starting index of the quad.
     * @return the optimized mesh.
     */
    private Spatial optimize(byte[][][] tiles, float scale, TileAtlas atlas, TileBank tileBank, Vector3Int startIndex) {
        // Total number of quads in the mesh.
        int numQuads = 0;

        // Count the number of quads
        for (int x = 0; x < optimizedTiles.length; x++) {
            for (int y = 0; y < optimizedTiles.length; y++) {
                for (int z = 0; z < optimizedTiles.length; z++) {
                    int tileQuads = setFacesPerDirection(optimizedTiles[x][y][z]);
                    numQuads += tileQuads;
                }
            }
        }

        // Total triangles of the mesh.
        int totalTriangles = numQuads * TRIANGLES_PER_QUAD;
        // Total vertices of the mesh.
        int totalVertices = numQuads * VERTICES_PER_QUAD;
        if (areQuintsUsed) {
            totalTriangles = numQuads * Quint.TRIANGLES;
            totalVertices = numQuads * Quint.VERTICES;
        }

        // Mesh to use for the combined data.
        Mesh mesh = new Mesh();

        // Create the index buffer
        Buffer data;
        VertexBuffer indexBuffer = new VertexBuffer(VertexBuffer.Type.Index);
        // Make sure all the meshes can fit.
        final int maxShortVertices = 65536;
        if (totalVertices >= maxShortVertices) {
            data = VertexBuffer.createBuffer(VertexBuffer.Format.UnsignedInt, COMPONENTS, totalTriangles);
            indexBuffer.setupData(VertexBuffer.Usage.Static, COMPONENTS, VertexBuffer.Format.UnsignedInt, data);
        } else {
            data = VertexBuffer.createBuffer(VertexBuffer.Format.UnsignedShort, COMPONENTS, totalTriangles);
            indexBuffer.setupData(VertexBuffer.Usage.Static, COMPONENTS, VertexBuffer.Format.UnsignedShort, data);
        }
        mesh.setBuffer(indexBuffer);

        // Set up the position buffer
        VertexBuffer positionBuffer = new VertexBuffer(VertexBuffer.Type.Position);
        positionBuffer.setupData(VertexBuffer.Usage.Static, COMPONENTS, VertexBuffer.Format.Float,
                VertexBuffer.createBuffer(VertexBuffer.Format.Float, COMPONENTS, totalVertices));
        mesh.setBuffer(positionBuffer);
        // Set up the normal buffer
        VertexBuffer normalBuffer = new VertexBuffer(VertexBuffer.Type.Normal);
        normalBuffer.setupData(VertexBuffer.Usage.Static, COMPONENTS, VertexBuffer.Format.Float,
                VertexBuffer.createBuffer(VertexBuffer.Format.Float, COMPONENTS, totalVertices));
        mesh.setBuffer(normalBuffer);
        // set up the texture buffer
        VertexBuffer textureBuffer = new VertexBuffer(VertexBuffer.Type.TexCoord);
        textureBuffer.setupData(VertexBuffer.Usage.Static, COMPONENTS_PER_TEXTURE, VertexBuffer.Format.Float,
                VertexBuffer.createBuffer(VertexBuffer.Format.Float, COMPONENTS_PER_TEXTURE, totalVertices));
        mesh.setBuffer(textureBuffer);

        // Quad to use for the transforms.
        //Quad quadMesh = new Quad(scale, scale);
        //Geometry quad = new Geometry("Quad", quadMesh);
        //quad.setMaterial(atlas.getMaterial());
        areQuintsUsed = true;
        Quint quintMesh = new Quint(scale, scale, midpoint);
        Geometry quint = new Geometry("Quint", quintMesh);
        quint.setMaterial(atlas.getMaterial());
        // Geometry and mesh to use as an intermediate.
        Geometry geometry = quint;
        Mesh faceMesh = quintMesh;
        if (areQuintsUsed) {
            midpoint.x = scale / 2;
            midpoint.y = scale / 2;
            midpoint.z = 0;
            quintMesh.setMidpoint(midpoint);
        }

        // Create the meshes.
        // Indices to use when appending to the index, first index is vertex, second is triangles.
        int[] indices = new int[]{0, 0};
        for (int x = 0; x < optimizedTiles.length; x++) {
            for (int y = 0; y < optimizedTiles.length; y++) {
                for (int z = 0; z < optimizedTiles.length; z++) {
                    int direction = optimizedTiles[x][y][z];
                    // Modify the quad given the direction and add to the buffers.
                    // Index converted to the tile map.
                    int mx = x + startIndex.x;
                    int my = y + startIndex.y;
                    int mz = z + startIndex.z;
                    TileBank.Tile tile = tileBank.getTile(tiles[mx][my][mz]);
                    if (direction != SIDE_NONE) {
                        // Prepare the quad to be copied for each direction.
                        if ((direction & SIDE_WEST) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.WEST, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.WEST);
                            copyMeshData(geometry, mesh, indices);
                        }
                        if ((direction & SIDE_EAST) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.EAST, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.EAST);
                            copyMeshData(geometry, mesh, indices);
                        }
                        if ((direction & SIDE_NORTH) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.NORTH, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.NORTH);
                            copyMeshData(geometry, mesh, indices);
                        }
                        if ((direction & SIDE_SOUTH) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.SOUTH, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.SOUTH);
                            copyMeshData(geometry, mesh, indices);
                        }
                        if ((direction & SIDE_UP) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.UP, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.UP);
                            copyMeshData(geometry, mesh, indices);
                        }
                        if ((direction & SIDE_DOWN) > 0) {
                            if (areQuintsUsed) {
                                midpoint.x = Random.randomFloat() * (tile.mx2 - tile.mx1) + tile.mx1;
                                midpoint.y = Random.randomFloat() * (tile.my2 - tile.my1) + tile.my1;
                                midpoint.z = Random.randomFloat() * (tile.mz2 - tile.mz1) + tile.mz1;
                                ((Quint) faceMesh).setTextureAligned(tile.isAligned);
                                ((Quint) faceMesh).setMidpoint(midpoint);
                            }
                            setQuadMappingsFromDirection(tiles[mx][my][mz], atlas, Direction.DOWN, faceMesh);
                            setQuadPositionFromGrid(geometry, mx, my, mz, scale, Direction.DOWN);
                            copyMeshData(geometry, mesh, indices);
                        }
                    }
                }
            }
        }
        // Update the bounds of the mesh.
        mesh.updateCounts();
        mesh.updateBound();

        Geometry optimizedGeometry = new Geometry("Chunk Mesh", mesh);
        optimizedGeometry.setLocalTransform(Transform.IDENTITY);
        optimizedGeometry.setMaterial(atlas.getMaterial());

        return optimizedGeometry;
    }

    /**
     * Sets the mapping for the quad's textures and normal.
     * @param type the type of tile.
     * @param atlas the atlas to get the texture information from.
     * @param quad the mesh to modify.
     * @param direction the direction of the quad.
     * TODO: Support normal maps and specular maps.
     */
    private void setQuadMappingsFromDirection(int type, TileAtlas atlas, Direction direction, Mesh quad) {
        if (direction != Direction.NONE) {
            switch (direction) {
                case WEST:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_WEST, texturePosition);
                    break;
                case EAST:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_EAST, texturePosition);
                    break;
                case NORTH:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_NORTH, texturePosition);
                    break;
                case SOUTH:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_SOUTH, texturePosition);
                    break;
                case UP:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_UP, texturePosition);
                    break;
                case DOWN:
                    texturePosition = atlas.getTextureFromIndex(type, INDEX_DOWN, texturePosition);
                    break;
                default:
                    throw new AssertionError("No matching direction found.");
            }
        }
        if (areQuintsUsed) {
            ((Quint) quad).setTexture(texturePosition.x1, texturePosition.x2, texturePosition.y1, texturePosition.y2);
        } else {
            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                    texturePosition.x1, texturePosition.y1,
                    texturePosition.x2, texturePosition.y1,
                    texturePosition.x2, texturePosition.y2,
                    texturePosition.x1, texturePosition.y2
            });
        }
    }

    /**
     * Retrieves a position from the grid without creating a new vector object.
     * @param geo the geometry to modify the location of.
     * @param x the x grid index.
     * @param y the y grid index.
     * @param z the z grid index.
     * @param scale the scale of each cube.
     * @param direction the direction of the quad.
     */
    private void setQuadPositionFromGrid(Geometry geo, int x, int y, int z, float scale, Direction direction) {
        float px = x * scale;
        float py = y * scale;
        float pz = z * scale;
        float rx = 0;
        float ry = 0;
        float rz = 0;

        switch (direction) {
            case WEST:
                ry = ANGLE_WEST;
                break;
            case EAST:
                ry = ANGLE_EAST;
                pz += scale;
                px += scale;
                break;
            case NORTH:
                ry = ANGLE_NORTH;
                px += scale;
                break;
            case SOUTH:
                ry = ANGLE_SOUTH;
                pz += scale;
                break;
            case UP:
                rx = ANGLE_UP;
                py += scale;
                pz += scale;
                break;
            case DOWN:
                rx = ANGLE_DOWN;
                break;
            default:
                throw new AssertionError("No matching direction.");
        }

        geo.setLocalTranslation(px, py, pz);
        rotation.fromAngles(rx, ry, rz);
        geo.setLocalRotation(rotation);
    }

    /**
     * Checks if a tile is surrounded by other tiles on all sides.
     * @param tiles the tiles to check with.
     * @param x the x index of the tile.
     * @param y the y index of the tile.
     * @param z the z index of the tile.
     * @return true if the tile is surrounded.
     */
    private boolean isTileSurrounded(byte[][][] tiles, int x, int y, int z) {
        // Check if within the grid bounds.
        boolean isValidIndex = (x - 1 >= 0 && x + 1 < tiles.length
                && y - 1 >= 0 && y + 1 < tiles[x].length
                && z - 1 >= 0 && z + 1 < tiles[x][y].length);

        if (isValidIndex) {
            // Check if the tile is surrounded.
            return (tiles[x - 1][y][z] != 0 && tiles[x + 1][y][z] != 0
                    && tiles[x][y - 1][z] != 0 && tiles[x][y + 1][z] != 0
                    && tiles[x][y][z - 1] != 0 && tiles[x][y][z + 1] != 0);
        }
        return false;
    }

    /**
     * Retrieves the directions a tile that should be visible.
     * @param tiles the tiles of the world.
     * @param x the x index of the tile.
     * @param y the y index of the tile.
     * @param z the z index of the tile.
     * @return the directions of the tile.
     */
    private byte getTileVisibleDirections(byte[][][] tiles, int x, int y, int z) {
        int direction = SIDE_NONE;

        // Find the visible direction by checking for sides that are not bordering tiles.
        // Create the left quad
        if (x > 0 && tiles[x - 1][y][z] == 0) {
            direction = direction | SIDE_WEST;
        } else if (x == 0) {
            direction = direction | SIDE_WEST;
        }
        // Create the right quad
        if (x < tiles.length - 1 && tiles[x + 1][y][z] == 0) {
            direction = direction | SIDE_EAST;
        } else if (x == tiles.length - 1) {
            direction = direction | SIDE_EAST;
        }
        // Create the back quad
        if (z > 0 && tiles[x][y][z - 1] == 0) {
            direction = direction | SIDE_NORTH;
        } else if (z == 0) {
            direction = direction | SIDE_NORTH;
        }
        // Create the front quad
        if (z < tiles[x][y].length - 1 && tiles[x][y][z + 1] == 0) {
            direction = direction | SIDE_SOUTH;
        } else if (z == tiles[x][y].length - 1) {
            direction = direction | SIDE_SOUTH;
        }
        // Create the top quad
        if (y < tiles[x].length - 1 && tiles[x][y + 1][z] == 0) {
            direction = direction | SIDE_UP;
        } else if (y == tiles[x].length - 1) {
            direction = direction | SIDE_UP;
        }
        // Create the bottom quad
        if (y > 0 && tiles[x][y - 1][z] == 0) {
            direction = direction | SIDE_DOWN;
        } else if (y == 0) {
            direction = direction | SIDE_DOWN;
        }

        return (byte) direction;
    }

    /**
     * Gets the number of quads per tile direction.
     * @param direction the direction of the tile.
     * @return the number of quads based on the direction.
     */
    private int setFacesPerDirection(byte direction) {
        int faces = 0;

        if (direction != SIDE_NONE) {
            if ((direction & SIDE_WEST) > 0) {
                faces++;
            }
            if ((direction & SIDE_EAST) > 0) {
                faces++;
            }
            if ((direction & SIDE_NORTH) > 0) {
                faces++;
            }
            if ((direction & SIDE_SOUTH) > 0) {
                faces++;
            }
            if ((direction & SIDE_UP) > 0) {
                faces++;
            }
            if ((direction & SIDE_DOWN) > 0) {
                faces++;
            }
        }

        return faces;
    }

    /**
     * Transforms to vertices.
     * @param inBuf the input buffer.
     * @param offset the offset amount in the buffer.
     * @param outBuf the output buffer.
     * @param transform the transform to use.
     */
    private static void doTransformVerts(FloatBuffer inBuf, int offset, FloatBuffer outBuf, Matrix4f transform) {
        Vector3f pos = new Vector3f();

        // offset is given in element units
        // convert to be in component units
        int currentOffset = offset * COMPONENTS;

        for (int i = 0; i < inBuf.limit() / COMPONENTS; i++) {
            pos.x = inBuf.get(i * COMPONENTS);
            pos.y = inBuf.get(i * COMPONENTS + 1);
            pos.z = inBuf.get(i * COMPONENTS + 2);

            transform.mult(pos, pos);

            outBuf.put(currentOffset + i * COMPONENTS + 0, pos.x);
            outBuf.put(currentOffset + i * COMPONENTS + 1, pos.y);
            outBuf.put(currentOffset + i * COMPONENTS + 2, pos.z);
        }
    }

    /**
     * Transform the normals.
     * @param inBuf the input buffer.
     * @param offset the offset in the buffer.
     * @param outBuf the output buffer.
     * @param transform the transform to use.
     */
    private static void doTransformNorms(FloatBuffer inBuf, int offset, FloatBuffer outBuf, Matrix4f transform) {
        Vector3f norm = new Vector3f();

        // offset is given in element units
        // convert to be in component units
        int currentOffset = offset * COMPONENTS;

        for (int i = 0; i < inBuf.limit() / COMPONENTS; i++) {
            norm.x = inBuf.get(i * COMPONENTS);
            norm.y = inBuf.get(i * COMPONENTS + 1);
            norm.z = inBuf.get(i * COMPONENTS + 2);

            transform.multNormal(norm, norm);

            outBuf.put(currentOffset + i * COMPONENTS + 0, norm.x);
            outBuf.put(currentOffset + i * COMPONENTS + 1, norm.y);
            outBuf.put(currentOffset + i * COMPONENTS + 2, norm.z);
        }
    }

    /**
     * @param areQuintsUsed true to use quints instead of quads.
     */
    public void setAreQuintsUsed(boolean areQuintsUsed) {
        this.areQuintsUsed = areQuintsUsed;
        setDirty();
    }

    @Override
    public void setDirty() {
        isOptimized = false;
    }

    @Override
    public void destroy(Spatial spatial) {
        isOptimized = false;
        optimizedTiles = null;
    }
}
