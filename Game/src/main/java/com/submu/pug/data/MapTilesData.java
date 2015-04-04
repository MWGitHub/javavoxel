package com.submu.pug.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/25/13
 * Time: 11:49 AM
 * Data that holds map tiles.
 */
public class MapTilesData {
    /**
     * Segments of tiles for the map.
     */
    public List<TileSegment> segments = new ArrayList<TileSegment>();

    /**
     * Represents a single tile segment.
     */
    public static class TileSegment {
        /**
         * Name of the tile segment.
         */
        public String name;

        /**
         * Dimensions of the segment.
         */
        public int dimensionX, dimensionY, dimensionZ;

        /**
         * Starting position of the segment.
         */
        public int startX = 0, startY = 0, startZ = 0;

        /**
         * Terrain represented as bytes with different tile types matching the textures.
         * In this format it is compressed and hashed.
         */
        public String tiles;

        /**
         * Decompressed tiles that are only used for development and do not appear in the JSON output.
         */
        public byte[][][] rawTiles;
    }
}
