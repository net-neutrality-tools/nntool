package at.alladin.nettest.service.map.domain.model.info.option;

import com.google.gson.annotations.Expose;

public class TimePeriodOption extends AbstractOption {
	
	public TimePeriodOption() {
		
	}
	
	public TimePeriodOption(final String title, final String summary, final Integer period) {
		this(title, summary, period, null);
	}
	
	public TimePeriodOption(final String title, final String summary, final Integer period, final Boolean isDefault) {
		this.period = period;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}

	@Expose
	private Integer period;

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}
	
}
