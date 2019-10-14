package at.alladin.nettest.service.map.util;

import at.alladin.nettest.service.map.domain.model.BoundingBox;
import at.alladin.nntool.shared.map.MapTileParameters;

public class GeographyHelper {

    // see
    // http://www.maptiler.org/google-maps-coordinates-tile-bounds-projection/
    private static final double MAX_EXTENT = 20037508.342789244;
    
    /**
     * @param zoom
     *            zoomlevel
     * @return resolution in meters per pixel
     */
    public static double getResFromZoom(final int tileSize, final int zoom) {
        final long powZoom = 1 << zoom;
        return MAX_EXTENT * 2 / tileSize / powZoom;
    }

    /**
     *
     * @param tileSize
     * @param x
     * @param y
     * @param zoom
     * @return
     */
    public static BoundingBox xyToMeters(final int tileSize, final int x, final int y, final int zoom) {
        final BoundingBox result = new BoundingBox();

        final long powZoom = 1 << zoom;
        result.setRes(MAX_EXTENT * 2 / tileSize / powZoom);
        final double w = MAX_EXTENT / powZoom * 2;
        final double myY = powZoom - 1 - y;

        result.setX1(x * w - MAX_EXTENT);
        result.setY1(myY * w - MAX_EXTENT);
        result.setX2((x + 1) * w - MAX_EXTENT);
        result.setY2((myY + 1) * w - MAX_EXTENT);

        return result;
    }

    public static BoundingBox xyToMeters (final MapTileParameters params) {
        return xyToMeters(TileHelper.getTileSizeLengthAt(TileHelper.getTileSizeIdx(params.getSize()))
        		, params.getX(), params.getY(), params.getZoom());
    }

    /**
     *
     * @param lat
     * @return
     */
    public static double latToMeters(final double lat) {
        return Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0) * MAX_EXTENT / 180.0;
    }

    /**
     *
     * @param lon
     * @return
     */
    public static double lonToMeters(final double lon) {
        return lon * MAX_EXTENT / 180.0;
    }
    
}
