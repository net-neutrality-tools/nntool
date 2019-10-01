package at.alladin.nettest.service.collector.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
@ConfigurationProperties(prefix = "external-services", ignoreUnknownFields = true)
public class ExternalServicesProperties {

    public static class DeviceImportSettings {
        private String name;

        private String cron;
        
        private String url;
        
        private boolean enabled = false;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
    }
    
    List<DeviceImportSettings> deviceImports = new ArrayList<>();

	public List<DeviceImportSettings> getDeviceImports() {
		return deviceImports;
	}

	public void setDeviceImports(List<DeviceImportSettings> deviceImports) {
		this.deviceImports = deviceImports;
	}
}
