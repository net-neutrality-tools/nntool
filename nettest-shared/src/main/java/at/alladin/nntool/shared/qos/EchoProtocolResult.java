/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
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

package at.alladin.nntool.shared.qos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nntool.shared.hstoreparser.annotation.HstoreKey;

/**
 * @author fk 
 */
public class EchoProtocolResult extends AbstractResult {

	@HstoreKey("echo_protocol_objective_host")
	@SerializedName("echo_protocol_objective_host")
	@Expose
	private Object host;

	@HstoreKey("echo_protocol_objective_port")
	@SerializedName("echo_protocol_objective_port")
	@Expose
	private Object port;

	@HstoreKey("echo_protocol_objective_protocol")
	@SerializedName("echo_protocol_objective_protocol")
	@Expose
	private Object protocol;

	@HstoreKey("echo_protocol_objective_payload")
	@SerializedName("echo_protocol_objective_payload")
	@Expose
	private Object payload;

	@HstoreKey("echo_protocol_status")
	@SerializedName("echo_protocol_status")
	@Expose
	private Object status;

	@HstoreKey("echo_protocol_result")
	@SerializedName("echo_protocol_result")
	@Expose
	private Object result;

	public Object getHost() {
		return host;
	}

	public void setHost(Object host) {
		this.host = host;
	}

	public Object getPort() {
		return port;
	}

	public void setPort(Object port) {
		this.port = port;
	}

	public Object getProtocol() {
		return protocol;
	}

	public void setProtocol(Object protocol) {
		this.protocol = protocol;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Object getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "EchoProtocolResult{" +
				"host=" + host +
				", port=" + port +
				", protocol=" + protocol +
				", payload=" + payload +
				", status=" + status +
				", result=" + result +
				'}';
	}
}
