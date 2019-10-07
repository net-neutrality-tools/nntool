package at.alladin.nntool.shared.map.info.option;

import com.google.gson.annotations.Expose;

public class DeviceOption extends AbstractOption {

	public DeviceOption() {
		
	}
	
	public DeviceOption(final String title, final String summary, final String device) {
		this(title, summary, device, null);
	}
	
	public DeviceOption(final String title, final String summary, final String device, final Boolean isDefault) {
		this.device = device;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	private String device;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
	
}
