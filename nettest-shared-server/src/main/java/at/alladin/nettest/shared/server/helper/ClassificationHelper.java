/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.helper;

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

