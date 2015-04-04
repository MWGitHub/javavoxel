package com.submu.pug.resources.map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/11/13
 * Time: 4:59 PM
 */
public final class MapUtils {
    /**
     * Do not allow instantiation.
     */
    private MapUtils() {
    }

    /**
     * Compresses the tiles and returns the output string.
     * @param tiles the tiles to compress.
     * @return the compressed string.
     * @throws java.io.IOException thrown when compression fails.
     */
    public static String compressTiles(byte[][][] tiles) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        byte[] singleTiles = new byte[tiles.length * tiles[0].length * tiles[0][0].length];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                for (int z = 0; z < tiles[x][y].length; z++) {
                    singleTiles[(x * tiles[x].length + y) * tiles[x][y].length + z] = tiles[x][y][z];
                }
            }
        }
        gzipOutputStream.write(singleTiles);
        gzipOutputStream.close();
        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }

    /**
     * Decompresses the tiles and returns a 3D byte array.
     * @param tiles the tiles to decompress.
     * @param xLength the first array length.
     * @param yLength the second array length.
     * @param zLength the third array length.
     * @return the decompressed tiles.
     * @throws IOException thrown when decompression fails.
     */
    public static byte[][][] decompressTiles(String tiles, int xLength, int yLength, int zLength) throws IOException {
        // Decompress the tiles.
        byte[] decodedTiles = Base64.decodeBase64(tiles);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream((decodedTiles)));
        IOUtils.copy(inputStream, byteArrayOutputStream);
        inputStream.close();
        decodedTiles = byteArrayOutputStream.toByteArray();
        byte[][][] rawTiles = new byte[xLength][yLength][zLength];
        for (int x = 0; x < rawTiles.length; x++) {
            for (int y = 0; y < rawTiles[x].length; y++) {
                for (int z = 0; z < rawTiles[x][y].length; z++) {
                    rawTiles[x][y][z] = decodedTiles[(x * rawTiles[x].length + y) * rawTiles[x][y].length + z];
                }
            }
        }
        return rawTiles;
    }
}
