package at.alladin.nntool.shared.map.info.option;

import com.google.gson.annotations.Expose;

public class OperatorOption extends AbstractOption {
	
	public OperatorOption() {
		
	}
	
	public OperatorOption(final String title, final String summary, final String operator) {
		this(title, summary, operator, null);
	}
	
	public OperatorOption(final String title, final String summary, final String operator, final Boolean isDefault) {
		this.operator = operator;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	private String operator;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
