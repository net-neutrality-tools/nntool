package at.alladin.nettest.service.loadbalancer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class LoadApiMem {

	@JsonProperty("buffers")
	String buffers;
	
	@JsonProperty("cached")	
	String cached;
	
	@JsonProperty("free")
	String free;
	
	@JsonProperty("total")
	String total;

	public String getBuffers() {
		return buffers;
	}

	public void setBuffers(String buffers) {
		this.buffers = buffers;
	}

	public String getCached() {
		return cached;
	}

	public void setCached(String cached) {
		this.cached = cached;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
}
