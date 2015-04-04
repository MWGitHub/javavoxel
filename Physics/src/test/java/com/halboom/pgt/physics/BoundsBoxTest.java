package com.halboom.pgt.physics;

import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.math.Vector3f;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/25/13
 * Time: 9:03 PM
 */
public class BoundsBoxTest {
    @Test
    /**
     * Test setting extents
     */
    public void testExtents() throws Exception {
        // Test extents from creation
        BoundsBox bounds = new BoundsBox(new Vector3f(), 0.25f, 0.5f, 0.75f);
        Assert.assertEquals(0.25f, bounds.getXExtent(), 0.01f);
        Assert.assertEquals(0.5f, bounds.getYExtent(), 0.01f);
        Assert.assertEquals(0.75f, bounds.getZExtent(), 0.01f);

        // Test extents from min/max setting
        bounds.setMinMax(new Vector3f(0f, -1f, 0.5f), new Vector3f(1f, 2.5f, 0.5f));
        Assert.assertEquals(0.5f, bounds.getCenter().x, 0.01f);
        Assert.assertEquals(0.75f, bounds.getCenter().y, 0.01f);
        Assert.assertEquals(0.5f, bounds.getCenter().z, 0.01f);
        Assert.assertEquals(0.5f, bounds.getXExtent(), 0.01f);
        Assert.assertEquals(1.75f, bounds.getYExtent(), 0.01f);
        Assert.assertEquals(0f, bounds.getZExtent(), 0.01f);

        // Test extents from single setting.
        bounds = new BoundsBox(new Vector3f(), 0.25f, 0.5f, 0.75f);
        bounds.setXExtent(1f);
        Assert.assertEquals(1f, bounds.getXExtent(), 0.01f);
        bounds.setYExtent(0.5f);
        Assert.assertEquals(0.5f, bounds.getYExtent(), 0.01f);
        bounds.setZExtent(60f);
        Assert.assertEquals(1f, bounds.getXExtent(), 0.01f);
        Assert.assertEquals(0.5f, bounds.getYExtent(), 0.01f);
        Assert.assertEquals(60f, bounds.getZExtent(), 0.01f);
        Assert.assertEquals(0f, bounds.getCenter().x, 0.01f);
        Assert.assertEquals(0f, bounds.getCenter().y, 0.01f);
        Assert.assertEquals(0f, bounds.getCenter().z, 0.01f);
    }

    @Test
    /**
     * Test initial center point setting and center point moving.
     */
    public void testSetCenterPoint() throws Exception {
        BoundsBox bounds = new BoundsBox(new Vector3f(), 0.25f, 0.5f, 0.75f);

        Assert.assertEquals(0f, bounds.getCenter().x, 0.01f);
        Assert.assertEquals(0f, bounds.getCenter().y, 0.01f);
        Assert.assertEquals(0f, bounds.getCenter().z, 0.01f);

        bounds.setCenter(new Vector3f(0.1f, 0.2f, -0.3f));
        Assert.assertEquals(0.1f, bounds.getCenter().x, 0.01f);
        Assert.assertEquals(0.2f, bounds.getCenter().y, 0.01f);
        Assert.assertEquals(-0.3f, bounds.getCenter().z, 0.01f);
    }

    @Test
    public void testAddBounds() throws Exception {
        BoundsBox bounds = new BoundsBox(new Vector3f(-1.5f, 0f, 3.5f), 1f, 0.5f, 4f);

        // Test extent adding
        BoundsBox addedBounds = bounds.addBounds(-0.5f, 0.5f, -1f);
        // Check the new extents
        Assert.assertEquals(1.25f, addedBounds.getXExtent(), 0.01f);
        Assert.assertEquals(0.75f, addedBounds.getYExtent(), 0.01f);
        Assert.assertEquals(4.5f, addedBounds.getZExtent(), 0.01f);
        // Check the new Centers
        Assert.assertEquals(-1.75f, addedBounds.getCenter().getX(), 0.01f);
        Assert.assertEquals(0.25f, addedBounds.getCenter().getY(), 0.01f);
        Assert.assertEquals(3f, addedBounds.getCenter().getZ(), 0.01f);
    }
}
