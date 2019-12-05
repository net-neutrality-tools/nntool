package at.alladin.nettest.service.map.web.api.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.version.VersionResponse;
import at.alladin.nettest.shared.server.web.api.v1.AbstractVersionResource;

/**
 * This controller is used to provide version information.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/versions")
public class VersionResource extends AbstractVersionResource {

	@Override
	public VersionResponse getVersionResponse() {
		final VersionResponse versionResponse = new VersionResponse();
		versionResponse.setMapServiceVersion(getVersionInformation());

		return versionResponse;
	}
}
