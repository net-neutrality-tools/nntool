package at.alladin.nettest.service.map.domain.model.info.option;

import com.google.gson.annotations.Expose;

public class ProviderOption extends AbstractOption {
	
	public ProviderOption() {
		
	}
	
	public ProviderOption(final String title, final String summary, final String provider) {
		this(title, summary, provider, null);
	}
	
	public ProviderOption(final String title, final String summary, final String provider, final Boolean isDefault) {
		this.provider = provider;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	private String provider;

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
