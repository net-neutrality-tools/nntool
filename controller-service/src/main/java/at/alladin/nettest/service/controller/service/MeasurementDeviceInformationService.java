/*******************************************************************************
 * Copyright 2016-2019 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nettest.service.controller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import ua_parser.Client;
import ua_parser.Parser;

import static at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto.DESKTOP;

/**
 * taken from the SpeedMeasurementResource
 * (https://github.com/alladin-IT/nettest-ilr/blob/2ec6b487b994114fc71243421346a684464922f7/control-server/src/main/java/at/alladin/nettest/server/control/web/api/v1/SpeedMeasurementResource.java)
 * 
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
@Service
public class MeasurementDeviceInformationService {

    private static final Logger logger = LoggerFactory.getLogger(MeasurementDeviceInformationService.class);

    private static Parser userAgentParser;

    static {
        try {
            userAgentParser = new Parser(MeasurementDeviceInformationService.class.getResourceAsStream("/config/ua_regexes.yaml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillDeviceInformation (final ApiRequestInfo apiRequestInfo, final HttpServletRequest request) {
        if (apiRequestInfo != null && apiRequestInfo.getAgentType() == DESKTOP) {
            // Got request from websocket client -> fill in info based on user agent
            final String ua = request.getHeader("user-agent");
            if (ua != null) {
                final Client client = userAgentParser.parse(ua);
                logger.debug("Parsed info from ua: {}", client.toString());
                if (client.os != null && apiRequestInfo.getOsVersion() == null) {
                    final StringBuilder vers = new StringBuilder();
                    if (client.os.family != null) {
                        vers.append(client.os.family);
                    }
                    if (client.os.major != null) {
                        if (vers.length() > 0) {
                            vers.append(" ");
                        }
                        vers.append(client.os.major);

                        if (client.os.minor != null) {
                            vers.append(".");
                            vers.append(client.os.minor);

                            if (client.os.patch != null) {
                                vers.append(".");
                                vers.append(client.os.patch);

                                if (client.os.patchMinor != null) {
                                    vers.append(".");
                                    vers.append(client.os.patchMinor);
                                }
                            }
                        }
                    }
                    if (vers.length() > 0) {
                        apiRequestInfo.setOsVersion(vers.toString());
                    }
                }
                if (client.userAgent != null) {
                    if (apiRequestInfo.getModel() == null) {
                        apiRequestInfo.setModel(client.userAgent.major + "." + client.userAgent.minor + "." + client.userAgent.patch);
                    }
                    if (apiRequestInfo.getCodeName() == null) {
                        apiRequestInfo.setCodeName(client.userAgent.family);
                    }
                }

            }
        }
    }
}
