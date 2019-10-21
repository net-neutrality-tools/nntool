package at.alladin.nettest.shared.server.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

//import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.Settings.ColorThresholds;
import at.alladin.nettest.shared.model.Settings.ThresholdsPerTechnology;

//TODO: move the stuff in this class into their own class
public class ClassificationHelper {

    public static enum ClassificationType {
    	UPLOAD,
    	DOWNLOAD,
    	PING,
    	SIGNAL
//    	SIGNAL_MOBILE,
//    	SIGNAL_WIFI,
//    	SIGNAL_RSRP
    }
    
    public static class ClassificationItem {
    	
    	private String classificationColor;
    	
    	private int classificationNumber;

		public String getClassificationColor() {
			return classificationColor;
		}

		public void setClassificationColor(String classificationColor) {
			this.classificationColor = classificationColor;
		}

		public int getClassificationNumber() {
			return classificationNumber;
		}

		public void setClassificationNumber(int classificationNumber) {
			this.classificationNumber = classificationNumber;
		}

		@Override
		public String toString() {
			return "ClassificationItem [classificationColor=" + classificationColor + ", classificationNumber="
					+ classificationNumber + "]";
		}
		
    }
    
}

