package at.alladin.nettest.service.map.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;

@Service
public class ColorMapperService {
	
	@Autowired
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;

    /**
     *
     */
    private SpeedThresholds thresholdsPerClassificationType;

    /**
     *
     */
    private SpeedThresholds skewedThresholdsPerClassificationType;

    @PostConstruct
    public void init() {
        this.thresholdsPerClassificationType = thresholdsHelperService.getThresholdsPerTechnology();
        //use skewed thresholds for nicer interpolation results
        this.skewedThresholdsPerClassificationType = thresholdsHelperService.getSkewedThresholdsPerTechnology();
    }

    protected boolean hasTechnologyEntry(final String technology, final MapServiceOptions.SignalGroup signalGroup, final ClassificationHelper.ClassificationType classificationType) {
        if(this.thresholdsPerClassificationType == null || thresholdsHelperService.getByClassificationType(thresholdsPerClassificationType, classificationType) == null) {
            return false;
        } else {
            return thresholdsHelperService.getByClassificationType(thresholdsPerClassificationType, classificationType) != null;
        }
    }

    /**
     *
     * @param colors
     * @param intervals
     * @param value
     * @return
     */
    protected int valueToColor(final double value, final MapServiceOptions.SignalGroup signalGroup,
                               final ClassificationHelper.ClassificationType classificationType) {
    	final ColorThresholds colorThresholds = thresholdsHelperService.getByClassificationType(skewedThresholdsPerClassificationType, classificationType);

    	if (colorThresholds == null) {
    		throw new RuntimeException("Color mapper not initialized correctly");
    	}
    	
        //select colours the new way
        final Long ceilingKey = colorThresholds.getColorMap().ceilingKey((long) value);
        final Long floorKey = colorThresholds.getColorMap().floorKey((long) value);

        if(floorKey == null) {
            return Integer.parseInt(colorThresholds.getColorMap().get(ceilingKey), 16);
        }

        if(ceilingKey == null) {
            return Integer.parseInt(colorThresholds.getDefaultColor(), 16);
        }
        
        double factor = 0;

        if (ceilingKey == floorKey) {
            factor = 1;
        } else {
            factor = (value - floorKey) / (ceilingKey - floorKey);
        }

        int c0 = Integer.parseInt(colorThresholds.getColorMap().get(floorKey), 16);
        int c1 = Integer.parseInt(colorThresholds.getColorMap().get(ceilingKey), 16);

        //do some magic colour interpolating
        final int c0r = c0 >> 16;
        final int c0g = c0 >> 8 & 0xff;
        final int c0b = c0 & 0xff;

        final int r = (int) (c0r + ((c1 >> 16) - c0r) * (factor)) ;
        final int g = (int) (c0g + ((c1 >> 8 & 0xff) - c0g) * (factor));
        final int b = (int) (c0b + ((c1 & 0xff) - c0b) * (factor));
        return r << 16 | g << 8 | b;
    }

    /**
     * Calculates a colour as the valueToColor method, only the resulting colour is NOT interpolated between the two
     * closest colours, but simply assigned according to the cutoff logic
     * @param value
     * @param technology
     * @param signalGroup
     * @param classificationType
     * @param colors
     * @param intervals
     * @return
     */
    protected int valueToDiscreteColor(final double value, final MapServiceOptions.SignalGroup signalGroup,
                                       final ClassificationHelper.ClassificationType classificationType) {
        final ColorThresholds colorThresholds = thresholdsHelperService.getByClassificationType(thresholdsPerClassificationType, classificationType);
        
    	if (colorThresholds == null) {
    		throw new RuntimeException("Color mapper not initialized correctly");
    	}

        final Long ceilingKey = colorThresholds.getColorMap().ceilingKey((long) value);

        if(ceilingKey == null) {
            return Integer.parseInt(colorThresholds.getDefaultColor(), 16);
        }

        return Integer.parseInt(colorThresholds.getColorMap().get(ceilingKey), 16);
        
    }

}
