package com.halboom.pgt.resources;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/9/13
 * Time: 6:33 PM
 */
public class ResourcePathsTest {
    /**
     * Paths to use for the tests.
     */
    private ResourcePaths resourcePaths;

    @Before
    public void setUp() throws Exception {
        resourcePaths = new ResourcePaths();
        resourcePaths.addToken("$user.dir$", System.getProperty("user.dir"));
        resourcePaths.addToken("$user.home$", System.getProperty("user.home"));
        resourcePaths.addToken("$user.name$", System.getProperty("user.name"));
        resourcePaths.addToken("$test$", "/Test/The/Token/");
        resourcePaths.addPath("Temp", "$user.dir$/PugGame/Temp/");
        resourcePaths.addPath("Test", "This$test$Token");
    }

    @Test
    public void testSetPath() throws Exception {
        resourcePaths.addPath("Path", "The/Path/");
        Assert.assertEquals("The/Path/", resourcePaths.getPath("Path"));
    }

    @Test
    public void testGetPath() throws Exception {
        Assert.assertEquals(resourcePaths.getPath("Temp"), System.getProperty("user.dir")
                + "/PugGame/Temp/");
        Assert.assertEquals(resourcePaths.getPath("Test"), "This/Test/The/Token/Token");
    }

    @Test
    public void testGetRawPath() throws Exception {
        Assert.assertEquals(resourcePaths.getRawPath("Test"), "This$test$Token");
        Assert.assertEquals(resourcePaths.getRawPath("Temp"), "$user.dir$/PugGame/Temp/");
    }

    @Test
    public void testRemovePath() throws Exception {
        Assert.assertNotNull(resourcePaths.getPath("Temp"));
        resourcePaths.removePath("Temp");
        Assert.assertNull(resourcePaths.getPath("Temp"));
    }

    @Test
    public void testAddToken() throws Exception {
        resourcePaths.addToken("normal", "buffalo");
        resourcePaths.addPath("normal", "normalnormalnormal");
        Assert.assertEquals(resourcePaths.getPath("normal"), "buffalobuffalobuffalo");
    }

    @Test
    public void testRemoveToken() throws Exception {
        Assert.assertEquals(resourcePaths.getPath("Test"), "This/Test/The/Token/Token");
        resourcePaths.removeToken("$test$");
        Assert.assertEquals(resourcePaths.getPath("Test"), "This$test$Token");
    }

    @Test
    public void testIsDirectoryWritable() throws Exception {
        Assert.assertTrue(ResourcePaths.isDirectoryWritable(System.getenv("APPDATA")
                + "/PugGame/InnerTest"));
        Assert.assertFalse(ResourcePaths.isDirectoryWritable("C:/Windows/Test"));
    }
}
