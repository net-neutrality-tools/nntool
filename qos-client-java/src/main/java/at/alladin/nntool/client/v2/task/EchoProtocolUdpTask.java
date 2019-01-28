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

package at.alladin.nntool.client.v2.task;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.result.QoSTestResultEnum;

/**
 * 
 * @author lb
 *
 */
public class EchoProtocolUdpTask extends AbstractEchoProtocolTask {

	/**
	 *
	 * @param taskDesc
	 */
	public EchoProtocolUdpTask(QualityOfServiceTest nnTest, TaskDesc taskDesc, int threadId) {
		super(nnTest, taskDesc, threadId, threadId);

		this.testPort = taskDesc.getPort();

		String value = (String) taskDesc.getParams().get(PARAM_TIMEOUT);
		this.timeout = value != null ? Long.valueOf(value) : DEFAULT_TIMEOUT;

		value = (String) taskDesc.getParams().get(PARAM_PAYLOAD);
		this.payload = value != null ? value : "default_payload";

		this.testHost = taskDesc.getHost();
		
	}

	/**
	 * 
	 */
	public QoSTestResult call() throws Exception {
		final QoSTestResult result = initQoSTestResult(QoSTestResultEnum.ECHO_PROTOCOL);
		try {
			onStart(result);

			if (testHost != null && testPort != null) {

				try (final DatagramSocket socket = new DatagramSocket()) {
					socket.connect(InetAddress.getByName(this.testHost), this.testPort);
					socket.setSoTimeout((int) (this.timeout / 1e6));

					final byte[] payloadBytes = payload.getBytes();

					final DatagramPacket sendPacket = new DatagramPacket(payloadBytes, payloadBytes.length, InetAddress.getByName(this.testHost), this.testPort);
					//increase the payload size to check for potentially added characters
					final DatagramPacket receivePacket = new DatagramPacket(new byte[payloadBytes.length + 1], payloadBytes.length + 1);

					socket.send(sendPacket);
					socket.receive(receivePacket);

					final String testResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
					if (sendPacket.getLength() == receivePacket.getLength() && payload.equals(testResponse)) {
                        result.getResultMap().put(RESULT, "OK");
                    } else {
                        result.getResultMap().put(RESULT, "ERROR");
                    }

					System.out.println("Echo Protocol TCP TEST response: " + testResponse);

					result.getResultMap().put(RESULT_STATUS, testResponse);

				} catch (SocketTimeoutException ex) {
					result.getResultMap().put(RESULT, "TIMEOUT");
				} catch (Exception ex) {
				    ex.printStackTrace();
					result.getResultMap().put(RESULT, "ERROR");
				}

			} else {
				result.getResultMap().put(RESULT, "ERROR");
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (this.testPort != null) {
				result.getResultMap().put(RESULT_PORT, testPort);
			}

			if (this.testHost != null) {
				result.getResultMap().put("echo_protocol_objective_host", this.testHost);
			}

			result.getResultMap().put(RESULT_TIMEOUT, timeout);

			result.getResultMap().put(AbstractEchoProtocolTask.PARAM_PAYLOAD, this.payload);

			result.getResultMap().put(AbstractEchoProtocolTask.RESULT_PROTOCOL, AbstractEchoProtocolTask.PROTOCOL_UDP);

			onEnd(result);
		}

		return result;
		    
	}

}
