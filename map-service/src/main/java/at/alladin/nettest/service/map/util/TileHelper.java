package at.alladin.nettest.service.map.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

public class TileHelper {

    //TODO: throw this into settings
    private static final int[] TILE_SIZES = new int[] { 256, 512, 768 };

    /**
     *
     */
    private static final byte[][] EMPTY_IMAGES = new byte[TILE_SIZES.length][];

    private static final AtomicBoolean emptyImagesInitialized = new AtomicBoolean(false);

    /**
     *
     */
    private static final byte[] EMPTY_MARKER = "EMPTY".getBytes();

    public static int getTileSizeIdx (final int size) {
        int tileSizeIdx = 0;
        for (int i = 0; i < TILE_SIZES.length; i++) {
            if (size == TILE_SIZES[i]) {
                tileSizeIdx = i;
                break;
            }
        }

        return tileSizeIdx;
    }

    public static int getTileSizeLength () {
        return TILE_SIZES.length;
    }

    /**
     * Return the tilesize length at the given index
     * @param index must be >= 0 and < getTileSizeLength()
     * @return the tilesize length, or -1 if the index was invalid
     */
    public static int getTileSizeLengthAt (final int index) {
        return index >= 0 && index < TILE_SIZES.length ? TILE_SIZES[index] : -1;
    }

    public static byte[] getEmptyMarker () {
        return EMPTY_MARKER;
    }

    public static byte[] getEmptyImage (final int size) {
        if (!emptyImagesInitialized.getAndSet(true)) {
            initializeEmptyImages();
        }
        return EMPTY_IMAGES[getTileSizeIdx(size)];
    }

    public static boolean isEmptyTile (final byte[] data) {
        return Arrays.equals(data, EMPTY_MARKER);
    }

    private static void initializeEmptyImages () {
        for (int i = 0; i < TILE_SIZES.length; i++) {
            final BufferedImage img = new BufferedImage(TILE_SIZES[i], TILE_SIZES[i], BufferedImage.TYPE_INT_ARGB);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                ImageIO.write(img, "png", baos);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            EMPTY_IMAGES[i] = baos.toByteArray();
        }
    }
}
