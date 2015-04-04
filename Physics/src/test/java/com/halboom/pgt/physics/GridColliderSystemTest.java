package com.halboom.pgt.physics;

import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.CollisionResolver;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.math.Vector3f;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/27/13
 * Time: 6:03 PM
 */
public class GridColliderSystemTest {
    private GridColliderSystem gridColliderSystem;
    private byte grid[][][];

    @Before
    /**
     * Create a simple map.
     */
    public void setUp() throws Exception {
        grid = new byte[32][32][32];
        float scale = 1.0f;
        gridColliderSystem = new GridColliderSystem(new EntitySystem(), new CollisionResolver());
        gridColliderSystem.setTiles(grid);
        gridColliderSystem.setScale(scale);

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                for (int z = 0; z < grid[x][y].length; z++) {
                    if (y < 4) {
                        grid[x][y][z] = 1;
                    } else {
                        grid[x][y][z] = 0;
                    }
                }
            }
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetPositionFromGrid() throws Exception {
        Vector3f position = gridColliderSystem.getPositionFromGrid(0, 0, 0);
        Assert.assertEquals(0.5f, position.x, 0.001f);
        Assert.assertEquals(0.5f, position.y, 0.001f);
        Assert.assertEquals(0.5f, position.z, 0.001f);

        gridColliderSystem.getPositionFromGrid(2, 3, 4);
        Assert.assertEquals(2.5f, position.x, 0.001f);
        Assert.assertEquals(3.5f, position.y, 0.001f);
        Assert.assertEquals(4.5f, position.z, 0.001f);
    }

    @Test
    public void testGetGridFromPosition() throws Exception {
        Vector3f position = new Vector3f();
        Vector3Int index = gridColliderSystem.getGridFromPosition(position.x, position.y, position.z);
        Assert.assertEquals(0, index.x);
        Assert.assertEquals(0, index.y);
        Assert.assertEquals(0, index.z);

        position.x = 2.5f;
        position.y = 3.99f;
        position.z = 4.0f;
        index = gridColliderSystem.getGridFromPosition(position.x, position.y, position.z);
        Assert.assertEquals(2, index.x);
        Assert.assertEquals(3, index.y);
        Assert.assertEquals(4, index.z);
    }

    @Test
    public void testGetTileIndicesFromBounds() throws Exception {
        BoundsBox bounds = new BoundsBox(new Vector3f(), 1.4f, 1.4f, 1.4f);

        Vector3Int minStore = new Vector3Int();
        Vector3Int maxStore = new Vector3Int();
        Assert.assertTrue(gridColliderSystem.getTileIndicesFromBounds(bounds, minStore, maxStore));
        Assert.assertEquals(0, minStore.x);
        Assert.assertEquals(0, minStore.y);
        Assert.assertEquals(0, minStore.z);
        Assert.assertEquals(1, maxStore.x);
        Assert.assertEquals(1, maxStore.y);
        Assert.assertEquals(1, maxStore.z);

        bounds.setCenter(new Vector3f(1.4f, -1.4f, 0));
        Assert.assertTrue(gridColliderSystem.getTileIndicesFromBounds(bounds, minStore, maxStore));
        Assert.assertEquals(0, minStore.x);
        Assert.assertEquals(0, minStore.y);
        Assert.assertEquals(0, minStore.z);
        Assert.assertEquals(2, maxStore.x);
        Assert.assertEquals(0, maxStore.y);
        Assert.assertEquals(1, maxStore.z);

        bounds.setCenter(new Vector3f(1.599999f, -1.399999f, 5f));
        Assert.assertTrue(gridColliderSystem.getTileIndicesFromBounds(bounds, minStore, maxStore));
        Assert.assertEquals(0, minStore.x);
        Assert.assertEquals(0, minStore.y);
        Assert.assertEquals(3, minStore.z);
        // 3 due to floating point tolerance.
        Assert.assertEquals(3, maxStore.x);
        Assert.assertEquals(0, maxStore.y);
        Assert.assertEquals(6, maxStore.z);

        bounds.setCenter(new Vector3f(0, 0, 100000f));
        Assert.assertFalse(gridColliderSystem.getTileIndicesFromBounds(bounds, minStore, maxStore));

        bounds = new BoundsBox(new Vector3f(1f, 2f, -3f), new Vector3f(16f, 5f, 8f));
        Assert.assertTrue(gridColliderSystem.getTileIndicesFromBounds(bounds, minStore, maxStore));
        // These are off by 1 due to floating point tolerance, remember to check for collisions on these.
        Assert.assertEquals(0, minStore.x);
        Assert.assertEquals(1, minStore.y);
        Assert.assertEquals(0, minStore.z);
        Assert.assertEquals(16, maxStore.x);
        Assert.assertEquals(5, maxStore.y);
        Assert.assertEquals(8, maxStore.z);
    }
}
