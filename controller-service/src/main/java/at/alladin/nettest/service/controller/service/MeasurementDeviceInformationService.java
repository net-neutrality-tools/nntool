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
