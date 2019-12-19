package at.alladin.nettest.service.map.service;

import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.map.config.MapCacheConfig;
import at.alladin.nettest.service.map.domain.model.BoundingBox;
import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.MapServiceSettings;
import at.alladin.nettest.service.map.domain.model.MapServiceSettings.SQLFilter;
import at.alladin.nettest.service.map.domain.model.TileImage;
import at.alladin.nettest.service.map.util.GeographyHelper;
import at.alladin.nettest.service.map.util.TileHelper;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nntool.shared.map.HeatmapTileParameters;
import at.alladin.nntool.shared.map.MapTileParameters;
import at.alladin.nntool.shared.map.MapTileType;

@Service
@CacheConfig(cacheNames = {"heatmapTile"})
public class HeatmapTileService {
	
	private final Logger logger = LoggerFactory.getLogger(HeatmapTileService.class);

	@Autowired
	private MapCacheConfig mapCacheConfig;

	@Autowired
	private MapOptionsService mapOptionsService;

	@Autowired
	private ClassificationService classificationService;

	@Autowired
	private ColorMapperService colorMapperService;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unchecked")
    private ThreadLocal<TileImage>[] tileImages;
	
	@SuppressWarnings("unchecked")
	private ThreadLocal<int[]>[] pixelBuffers = new ThreadLocal[TileHelper.getTileSizeLength()];

	private final static int[] ZOOM_TO_PART_FACTOR = new int[] {
			// factor | zoomlevel
			0, // 0
			0, // 1
			0, // 2
			0, // 3
			0, // 4
			0, // 5
			0, // 6
			1, // 7
			1, // 8
			2, // 9
			2, // 10
			3, // 11
			3, // 12
			4, // 13
			4, // 14
			5, // 15
			5, // 16
			6, // 17
			6, // 18
			7, // 19
			7, // 20
	};

	private final static double ALPHA_TOP = 0.5;
	private final static int ALPHA_MAX = 1;

	private final static boolean DEBUG_LINES = false;

	private final static int HORIZON_OFFSET = 1;
	private final static int HORIZON = HORIZON_OFFSET * 2 + 2;
	private final static int HORIZON_SIZE = HORIZON * HORIZON;

	private final static double[][] FACTORS = new double[8][]; // lookup table for speedup

	@PostConstruct
	public void init() {

		// TODO: rewrite this static beauty

		for (int f = 0; f < 8; f++) {
			final int partSize = 1 << f;
			FACTORS[f] = new double[HORIZON_SIZE * partSize * partSize];

			for (int i = 0; i < FACTORS[f].length; i += HORIZON_SIZE) {
				final double qPi = Math.PI / 4;

				final double x = qPi * (i / HORIZON_SIZE % partSize) / partSize;
				final double y = qPi * (i / HORIZON_SIZE / partSize) / partSize;

				// double sum = 0;
				for (int j = 0; j < HORIZON; j++) {
					for (int k = 0; k < HORIZON; k++) {
						final double value = Math.pow(Math.cos(x + (1 - j) * qPi), 2.0)
								* Math.pow(Math.cos(y + (1 - k) * qPi), 2.0) / 4;
						FACTORS[f][i + j + k * HORIZON] = value;
						// sum += value;
					}
				}
			}
		}

		for (int i = 0; i < TileHelper.getTileSizeLength(); i++) {
			final int tileSize = TileHelper.getTileSizeLengthAt(i);
			pixelBuffers[i] = new ThreadLocal<int[]>() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.ThreadLocal#initialValue()
				 */
				@Override
				protected int[] initialValue() {
					return new int[tileSize * tileSize];
				};
			};
		}
		//TODO: possibly fix tileImages appearing thrice
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
	@Cacheable(condition = "#{mapCacheConfig.getUseHeatmapTileCache()}", unless = "#params.isNoCache() or #params.getZoom() >= {#mapOptionsService.getMaxZoomLevel()}")
	public byte[] generateTileData(final MapTileParameters mapParams) {

		if (mapParams == null || mapParams.getMapTileType() != MapTileType.HEATMAP
				|| !(mapParams instanceof HeatmapTileParameters)) {
			throw new IllegalArgumentException("Invalid parameters for requested HeatmapTile");
		}

		final HeatmapTileParameters params = (HeatmapTileParameters) mapParams;

		final MapServiceOptions options = mapOptionsService.getMapOptionsForKey(params.getMapOption());
		
		if (options == null) {
			throw new IllegalArgumentException ("Invalid map option: " + params.getMapOption());
		}
		
		if (params.getTransparency() == null) {
        	params.setTransparency(0.75);
        }

		final int tileSizeIdx = TileHelper.getTileSizeIdx(params.getSize());

		final List<MapServiceSettings.SQLFilter> sqlFilterList = mapOptionsService
				.getSqlFilterList(params.getFilterMap(), true, true);

		final int tileSize = TileHelper.getTileSizeLengthAt(tileSizeIdx);

		final double transparency = params.getTransparency();

		final StringBuilder whereSQL = new StringBuilder(options.getSqlFilter());
		for (final MapServiceSettings.SQLFilter sf : sqlFilterList) {
			whereSQL.append(" AND ").append(sf.getWhereClause());
		}
		
		// instead of the quantile we now take the interpolated value at that quantile,
		// for comparison's sake we now need the value, and not the valueLog
		final String sql = String.format("SELECT count(%1$s) count,"
				+ " percentile_cont(?) WITHIN GROUP (ORDER BY (%1$s)::float) percentile_value, "
				+ " ST_X(ST_SnapToGrid(t.geo_location_geometry, ?,?,?,?)) gx," 
				+ " ST_Y(ST_SnapToGrid(t.geo_location_geometry, ?,?,?,?)) gy "
				+ " FROM measurements t"
				+ " LEFT JOIN ias_measurements ias on t.open_data_uuid = ias.measurement_open_data_uuid"
				+ " WHERE "
				+ " %2$s"
				+ " AND t.geo_location_geometry && ST_SetSRID(ST_MakeBox2D(ST_Point(?,?), ST_Point(?,?)), 900913)"
				+ " GROUP BY gx,gy", options.getSqlValueColumn(), whereSQL);

		final int partSizeFactor;
		if (params.getZoom() >= ZOOM_TO_PART_FACTOR.length) {
			partSizeFactor = ZOOM_TO_PART_FACTOR[ZOOM_TO_PART_FACTOR.length - 1];
		} else {
			partSizeFactor = ZOOM_TO_PART_FACTOR[params.getZoom()];
		}

		final int partSizePixels = 1 << partSizeFactor;

		final int fetchPartsX = tileSize / partSizePixels + (HORIZON_OFFSET + 2) * 2;
		final int fetchPartsY = tileSize / partSizePixels + (HORIZON_OFFSET + 2) * 2;

		final double[] values = new double[fetchPartsX * fetchPartsY];
		final int[] countsRel = new int[fetchPartsX * fetchPartsY];

		Arrays.fill(values, Double.NaN);

		final BoundingBox bbox = GeographyHelper.xyToMeters(params);
		
		final double partSize = bbox.getRes() * partSizePixels;
		final double origX = bbox.getX1() - bbox.getRes() * (partSizePixels / 2) - partSize * (HORIZON_OFFSET + 1);
		final double origY = bbox.getY1() - bbox.getRes() * (partSizePixels / 2) - partSize * (HORIZON_OFFSET + 1);

		try {
			List<Integer> intList = jdbcTemplate.query(sql, ps -> {
				int p = 1;
				ps.setFloat(p++, params.getQuantile());

				for (int j = 0; j < 2; j++) {
					ps.setDouble(p++, origX);
					ps.setDouble(p++, origY);
					ps.setDouble(p++, partSize);
					ps.setDouble(p++, partSize);
				}

				for (final SQLFilter sf : sqlFilterList) {
					p = sf.fillParams(p, ps);
				}

				final double margin = partSize * (HORIZON_OFFSET + 1);
				ps.setDouble(p++, bbox.getX1() - margin);
				ps.setDouble(p++, bbox.getY1() - margin);
				ps.setDouble(p++, bbox.getX2() + margin);
				ps.setDouble(p++, bbox.getY2() + margin);

			}, (ResultSet rs, int rowNum) -> {
				int count = rs.getInt(1);
                final double val = rs.getDouble(2);
                final double gx = rs.getDouble(3);
                final double gy = rs.getDouble(4);
                final int mx = (int) Math.round((gx - origX) / partSize);
                final int my = (int) Math.round((gy - origY) / partSize);
                
                if (mx >= 0 && mx < fetchPartsX && my >= 0 && my < fetchPartsY) {
                    final int idx = mx + fetchPartsX * (fetchPartsY - 1 - my);
                    values[idx] = val;//val;
                    // countsReal[idx] = count;
                    if (count > ALPHA_MAX) {
                        count = ALPHA_MAX;
                    }
                    countsRel[idx] = count;
                }
				return rowNum;
			});
			
			if (intList.size() == 0) {
				logger.info("Requested heatmap tile contains no measurements. Returning empty image");
				return null;
			}
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			throw new IllegalStateException(ex);
		}

		final TileImage img = tileImages[tileSizeIdx].get();
		
		final int[] pixels = pixelBuffers[tileSizeIdx].get();
		for (int y = 0; y < tileSize; y++) {
			for (int x = 0; x < tileSize; x++) {
				final int mx = HORIZON_OFFSET + 1 + (x + partSizePixels / 2) / partSizePixels;
				final int my = HORIZON_OFFSET + 1 + (y + partSizePixels / 2) / partSizePixels;
				final int relX = (x + partSizePixels / 2) % partSizePixels;
				final int relY = (y + partSizePixels / 2) % partSizePixels;
				final int relOffset = (relY * partSizePixels + relX) * HORIZON_SIZE;

				double alphaWeigth = 0;
				double valueWeight = 0;
				double valueMissing = 0;
				final int startIdx = mx - HORIZON_OFFSET + fetchPartsX * (my - HORIZON_OFFSET);

				for (int i = 0; i < HORIZON_SIZE; i++) {
					final int idx = startIdx + i % HORIZON + fetchPartsX * (i / HORIZON);

					if (Double.isNaN(values[idx])) {
						valueMissing += FACTORS[partSizeFactor][i + relOffset];
					} else {
						valueWeight += FACTORS[partSizeFactor][i + relOffset] * values[idx];
					}

					alphaWeigth += FACTORS[partSizeFactor][i + relOffset] * countsRel[idx];
				}

				if (valueMissing > 0) {
					valueWeight += valueWeight / (1 - valueMissing) * valueMissing;
				}

				alphaWeigth /= ALPHA_TOP;
				if (alphaWeigth < 0) {
					alphaWeigth = 0;
				}
				if (alphaWeigth > 1) {
					alphaWeigth = 1;
				}

				alphaWeigth *= transparency;
				//TODO: move technology out of loop (is that safe to do?)
				final String technology;
                if (params.getFilterMap() != null) {
                	technology = params.getFilterMap().get("technology");
                } else {
                	technology = null;
                }

				final int alpha = (int) (alphaWeigth * 255) << 24;
				assert alpha >= 0 || alpha <= 255 : alpha;
				if (alpha == 0) {
					pixels[x + y * tileSize] = 0;
				} else {
					// In the ping case the thresholds are defined as ms, the values as ns => DIVIDE
					// ET IMPERAT!
					pixels[x + y * tileSize] = colorMapperService.valueToColor(options.getClassificationType() == ClassificationType.PING ? valueWeight / 1e6 : valueWeight,
							options.getSignalGroup(), options.getClassificationType())
							| alpha;
				}
				// pixels[x + y * WIDTH] = 255 << 24 | alpha >>> 8 |
				// alpha >>> 16 | alpha >>> 24;

				if (DEBUG_LINES) {
					if (relX == partSizePixels / 2 || relY == partSizePixels / 2) {
						pixels[x + y * tileSize] = 0xff000000;
					}
				}
			}
		}

		img.getBufferedImage().setRGB(0, 0, tileSize, tileSize, pixels, 0, tileSize);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(img.getBufferedImage(), "png", baos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		return baos.toByteArray();

	}

}
