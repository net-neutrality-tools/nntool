package at.alladin.nettest.service.map.service;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import at.alladin.nettest.service.map.config.MapCacheConfig;
import at.alladin.nettest.service.map.domain.model.BoundingBox;
import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.MapServiceSettings;
import at.alladin.nettest.service.map.domain.model.TileImage;
import at.alladin.nettest.service.map.util.GeographyHelper;
import at.alladin.nettest.service.map.util.TileHelper;
import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nntool.shared.map.MapTileParameters;
import at.alladin.nntool.shared.map.MapTileType;
import at.alladin.nntool.shared.map.PointTileParameters;

@CacheConfig(cacheNames = {"pointTile"})
@Service
public class PointTileService {
	
	private final Logger logger = LoggerFactory.getLogger(PointTileService.class);

    @Inject
    private MapCacheConfig mapCacheConfig;

    @Inject
    private MapOptionsService mapOptionsService;

    @Inject
    private ColorMapperService colorMapperService;

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ThreadLocal<TileImage>[] tileImages;

    @PostConstruct
    public void init() {
        tileImages = new ThreadLocal[TileHelper.getTileSizeLength()];
        for (int i = 0; i < TileHelper.getTileSizeLength(); i++) {
            final int tileSize = TileHelper.getTileSizeLengthAt(i);
            tileImages[i] = new ThreadLocal<TileImage>() {

                /*
                 * (non-Javadoc)
                 * @see java.lang.ThreadLocal#initialValue()
                 */
                @Override
                protected TileImage initialValue() {
                    final TileImage image = new TileImage();

                    image.setBufferedImage(new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB));
                    image.setGraphics2D(image.getBufferedImage().createGraphics());
                    image.getGraphics2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    image.getGraphics2D().setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
                    image.getGraphics2D().setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    image.getGraphics2D().setStroke(new BasicStroke(1f));
                    image.setWidth(image.getBufferedImage().getWidth());
                    image.setHeight(image.getBufferedImage().getHeight());

                    return image;
                }
            };
        }
    }

    //we could add a #result != null check, however I think we still want to cache empty tiles
    @Cacheable(condition = "#{mapCacheConfig.getUsePointTileCache()}", unless = "#params.isNoCache() or #params.getZoom() >= {#mapOptionsService.getMaxZoomLevel()}")
    public byte[] generateTileData(final MapTileParameters mapParams) {
        //List<MapServiceSettings.MapFilter> mapFilterList = mapOptionsService.getMapFilterListForKeys(params.getFilterList());

        if (mapParams == null || mapParams.getMapTileType() != MapTileType.POINTS
        		|| !(mapParams instanceof PointTileParameters)) {
        	throw new IllegalArgumentException("Invalid parameters for requested PointTile");
        }

        final PointTileParameters params = (PointTileParameters) mapParams;
        

        logger.info("computing points for mapoption: " + params.getMapOption());
        
        final MapServiceOptions options = mapOptionsService.getMapOptionsForKey(params.getMapOption());
        
        if (options == null) {
        	throw new IllegalArgumentException("provided map option not available: " + params.getMapOption());
        }

        final List<MapServiceSettings.SQLFilter> sqlFilterList = mapOptionsService.getSqlFilterList(params.getFilterMap(), true, true);

        //set some default options, if no parameters were provided
        if (params.getTransparency() == null) {
        	params.setTransparency(0.6);
        }
        
        final double diameter;
        if (params.getPointDiameter() != null) {
        	diameter = params.getPointDiameter();
        } else {
        	diameter = 12.0;
        	params.setPointDiameter(diameter);
        }

        if (params.isNoFill() == null) {
        	params.setNoFill(false);
        }
        
        if (params.isNoColor() == null) {
        	params.setNoColor(false);
        }

        final String hightlightUUIDString = params.getHighlight();
        UUID hightlightUUID = null;
        if (hightlightUUIDString != null) {
            try {
                hightlightUUID = UUID.fromString(hightlightUUIDString);
            } catch (final Exception e) {

            }
        }
        
        //and start calculating
        final double radius = diameter / 2d;
        final double triangleSide = diameter * 1.5;
        final double triangleHeight = Math.sqrt(3) / 2d * triangleSide;
        final int transparency = (int) Math.round(params.getTransparency() * 255);
        final boolean noFill = params.isNoFill();

        final Color borderColor = new Color(0, 0, 0, transparency);
        final Color highlightBorderColor = new Color(0, 0, 0, transparency);

        try {
            final List<Dot> dots = new ArrayList<>();

            final StringBuilder whereSQL = new StringBuilder(options.getSqlFilter());
            for (final MapServiceSettings.SQLFilter sf : sqlFilterList) {
                whereSQL.append(" AND ").append(sf.getWhereClause());
            }
            
            String sql = null;
            //If we classifiy mobile signals, we need to grab the lte_rsrp vals for the LTE measurements
            if (options.getClassificationType() == ClassificationHelper.ClassificationType.SIGNAL && options.getSignalGroup() == MapServiceOptions.SignalGroup.MOBILE) {
                sql = String.format(
                		"SELECT ST_X(t.geo_location_geometry) x, ST_Y(t.geo_location_geometry) y, t.mobile_network_lte_rsrq_dbm as lte_rsrp,  %s val "
        				//+ (hightlightUUID == null ? "" : ", t.agent_uuid ")
                        + ", initial_network_type_id as network_type_id, to_json(network_signal_info) as signals "
                        + " FROM measurements t"
                        + " LEFT JOIN ias_measurements ias on t.open_data_uuid = ias.measurement_open_data_uuid"
                        //+ (hightlightUUID == null ? "" : " LEFT JOIN ha_client c ON (t.client_uuid=c.uuid AND c.uuid=?::uuid)")
                        + " WHERE "
                        + " %s"
                        + " AND t.geo_location_geometry && ST_SetSRID(ST_MakeBox2D(ST_Point(?,?), ST_Point(?,?)), 900913)"
                        + " ORDER BY"
                        //+ (hightlightUUID == null ? "" : " c.uuid DESC, ") 
                        + " t.open_data_uuid", options.getSqlValueColumn(), whereSQL);
            } else {
                sql = String.format(
                		"SELECT ST_X(t.geo_location_geometry) x, ST_Y(t.geo_location_geometry) y, %1$s val "
                		//+ (hightlightUUID == null ? "" : ", t.agent_uuid ")
                        + ", initial_network_type_id as network_type_id, to_json(network_signal_info) as signals "
                        + " FROM measurements t"
                        + " LEFT JOIN ias_measurements ias on t.open_data_uuid = ias.measurement_open_data_uuid"
//                        + (hightlightUUID == null ? "" : " LEFT JOIN ha_client c ON (t.client_uuid=c.uuid AND c.uuid=?::uuid)")
                        + " WHERE "
                        //+ (hightlightUUID == null ? "" : " t.agent_uuid = ?::uuid AND ")
                        + " %2$s"
                        + " AND t.geo_location_geometry && ST_SetSRID(ST_MakeBox2D(ST_Point(?,?), ST_Point(?,?)), 900913)"
                        + " ORDER BY"
                        //+ (hightlightUUID == null ? "" : " t.agent_uuid DESC, ") 
                        + " t.open_data_uuid", options.getSqlValueColumn(), whereSQL);
            }
            logger.info(sql);

            final UUID finalUuid = null;//hightlightUUID; //highlightUuid is temporarily disabled
            final BoundingBox bbox = GeographyHelper.xyToMeters(params);

            try {
                jdbcTemplate.query(sql, ps -> {

	                    int i = 1;
	
	                    if (finalUuid != null) {
	                        ps.setObject(i++, finalUuid.toString());
	                    }
	
	                    for (final MapServiceSettings.SQLFilter sf : sqlFilterList) {
	                        i = sf.fillParams(i, ps);
	                    }
	
	                    final double margin = bbox.getRes() * triangleSide;
	                    ps.setDouble(i++, bbox.getX1() - margin);
	                    ps.setDouble(i++, bbox.getY1() - margin);
	                    ps.setDouble(i++, bbox.getX2() + margin);
	                    ps.setDouble(i++, bbox.getY2() + margin);
	
	                },
	                (ResultSet rs, int rowNum)-> {
	
	                    int index = 1;
	                    final double cx = rs.getDouble(index++);
	                    final double cy = rs.getDouble(index++);
	
	                    //if the measurement is a 4G measurement -> use lte_rsrp instead of the normal signal
	                    long lteSignal = 1;
	                    if (options.getClassificationType() == ClassificationHelper.ClassificationType.SIGNAL && options.getSignalGroup() == MapServiceOptions.SignalGroup.MOBILE) {
	                        lteSignal = rs.getLong(index++);
	                    }
	
	                    long value = rs.getLong(index++);
	
	
	                    final boolean highlight;
	                    if (finalUuid != null) {
	                        final Object clientUUID = rs.getObject(index++);
	                        highlight = clientUUID != null;
	                    } else {
	                        highlight = false;
	                    }
	
	                    //get the first networkType
	                    String signalString = rs.getString("signals");
	                    final JSONArray signalArr;
	                    if (signalString != null) {
	                    	signalArr = new JSONArray(rs.getString("signals"));
	                    } else {
	                    	signalArr = new JSONArray();
	                    }
	
	                    if (lteSignal < 0) {
	                        value = lteSignal;
	                    }
	
	                    //In the ping case the thresholds are defined as ms, the values as ns => DIVIDE ET IMPERAT!
	                    //Comment the code below in to get the point tiles with colours matching the actual used technology (matching the popup)
	                    final Color color = new Color(colorMapperService.valueToDiscreteColor(options.getClassificationType() == ClassificationHelper.ClassificationType.PING ? value / 1e6 : value,
	                    		options.getSignalGroup(), options.getClassificationType()));
	                    /*
	                     //Comment the code below in to get the point tiles with colours matching the chosen legend (values depending on the map filter, not the actual technology)
	                    try {
	                        color = Color.decode("#" + item.getClassificationColor());
	                    } catch (NumberFormatException ex) {
	                        ex.printStackTrace();
	                        //default colour set to gray
	                        color = colorGray;
	                    }
	                    */
	
	                    dots.add(new Dot(cx, cy, color, highlight));
	                    return null;
	                });
            } catch (DataAccessException ex) {
                throw new IllegalArgumentException(ex);
            }
            
            if (dots.size() == 0) {
            	logger.info("Requested point tile contains no measurements. Returning empty image");
            	return null;
            }

            final int tileSizeIdx = TileHelper.getTileSizeIdx(params.getSize());
            final TileImage img = tileImages[tileSizeIdx].get();
            final Graphics2D g = img.getGraphics2D();

            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, img.getWidth(), img.getHeight());
            g.setComposite(AlphaComposite.Src);
            g.setStroke((new BasicStroke(((float) diameter / 8f))));

            final Path2D.Double triangle = new Path2D.Double();
            final Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, diameter, diameter);

            for (final Dot dot : dots) {
                final double relX = (dot.x - bbox.getX1()) / bbox.getRes();
                final double relY = TileHelper.getTileSizeLengthAt(tileSizeIdx) - (dot.y - bbox.getY1()) / bbox.getRes();

                if (dot.highlight) {
                    triangle.reset();
                    triangle.moveTo(relX, relY - triangleHeight / 3 * 2);
                    triangle.lineTo(relX - triangleSide / 2, relY + triangleHeight / 3);
                    triangle.lineTo(relX + triangleSide / 2, relY + triangleHeight / 3);
                    triangle.closePath();

                    if (!noFill) {
                        g.setPaint(dot.color);
                        g.fill(triangle);
                    }

                    g.setPaint(highlightBorderColor);
                    g.draw(triangle);
                } else {
                    shape.x = relX - radius;
                    shape.y = relY - radius;

                    if (!noFill) {
                        g.setPaint(dot.color);
                        g.fill(shape);
                    }

                    g.setPaint(borderColor);
                    g.draw(shape);
                }
            }

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img.getBufferedImage(), "png", baos);
            return baos.toByteArray();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }

    }

    private static class Dot {

        private double x;

        private double y;

        private Color color;

        private boolean highlight;

        private Dot () {

        }

        private Dot(final double x, final double y, final Color color, final boolean highlight) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.highlight = highlight;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public boolean isHighlight() {
            return highlight;
        }

        public void setHighlight(boolean highlight) {
            this.highlight = highlight;
        }
    }

}
