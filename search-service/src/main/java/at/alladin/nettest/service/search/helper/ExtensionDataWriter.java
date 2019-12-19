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

package at.alladin.nettest.service.search.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.service.search.config.ExportProperties;

/**
 * Interface which needs to be implemented for every file extension used in export.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface ExtensionDataWriter {

	void write(List<Map<String, Object>> data, ExportProperties exportProperties, ExportExtension ext, OutputStream outputStream) throws IOException;
}
