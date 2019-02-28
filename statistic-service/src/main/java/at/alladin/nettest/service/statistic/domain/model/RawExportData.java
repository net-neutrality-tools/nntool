package at.alladin.nettest.service.statistic.domain.model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class RawExportData {
	
	/**
	 * 
	 */
	@Expose
	List<String> columns;
    
	/**
	 * 
	 */
	@Expose
	List<List<String>> data;

	/**
	 * 
	 * @return
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * 
	 * @param columns
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * 
	 * @return
	 */
	public List<List<String>> getData() {
		return data;
	}

	/**
	 * 
	 * @param data
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
