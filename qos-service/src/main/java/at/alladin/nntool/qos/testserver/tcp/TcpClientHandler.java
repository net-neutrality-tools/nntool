/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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

package at.alladin.nntool.qos.testserver.tcp;

import java.io.DataInputStream;
import java.io.FilterOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.TestServer;
import at.alladin.nntool.qos.testserver.tcp.competences.Action;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;
import at.alladin.nntool.qos.testserver.tcp.competences.ResponseAction;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;

/**
 * 
 * @author lb
 *
 */
public class TcpClientHandler implements Runnable {
	
	/**
	 * 
	 */
	public final static int TCP_HANDLER_TIMEOUT = 10000;

	/**
	 * 
	 */
	private final Socket clientSocket;
	
	private final String name;
	
	/**
	 * 
	 */
	private final AtomicReference<TcpMultiClientServer> tcpServer;
	
	private final AtomicBoolean repeat = new AtomicBoolean(false);
	
	public TcpClientHandler(Socket clientSocket, TcpMultiClientServer tcpServer) {
		this.clientSocket = clientSocket;
		this.tcpServer = new AtomicReference<TcpMultiClientServer>(tcpServer);
		this.name = "[TcpClientHandler " + clientSocket.getInetAddress().toString() + "]";
	}
	
	@Override
	public void run() {
		TestServerConsole.log("New TCP ClientHander Thread started. Client: " + clientSocket, 1, TestServerServiceEnum.TCP_SERVICE);
		
		//try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
				FilterOutputStream fos = new FilterOutputStream(clientSocket.getOutputStream());) {
			clientSocket.setSoTimeout(TCP_HANDLER_TIMEOUT);
			
			boolean validCandidate = false;
	
			if (TestServer.getInstance().serverPreferences.isIpCheck()) {
				//check for test candidate if ip check is enabled
				if (!tcpServer.get().getCandidateMap().containsKey(clientSocket.getInetAddress())) {
					if (clientSocket != null && !clientSocket.isClosed()) {
						clientSocket.close();
					}
	
					TestServerConsole.log(clientSocket.getInetAddress() + ": not a valid candidate for TCP/NTP", 
							TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
				}
				else {
					validCandidate = true;
				}
			}
			else {
				//else allow connection
				validCandidate = true;
			}
			
			if (validCandidate) {
				//remove test candidate if ip check is enabled
				if (TestServer.getInstance().serverPreferences.isIpCheck()) {
					tcpServer.get().removeCandidate(clientSocket.getInetAddress());
				}

				do {
					repeat.set(false);
					tcpServer.get().refreshTtl(TcpMultiClientServer.TTL);
										
					//String clientRequest = br.readLine();
					final int availableLen = dis.available();
					final byte[] buffer = new byte[availableLen];
					final int len = dis.read(buffer);
					if (len < 0) {
						break;
					}
					
					TestServerConsole.log("TCP/NTP Server (" + tcpServer.get().getServerSocket() + ") (:" + tcpServer.get().getPort() + "), connection from: " + clientSocket.getInetAddress().toString() + ", request: " + new String(buffer), 
							TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
		
					//check competences and send echo or other response
					List<Action> response = null;
					final Iterator<Competence> compIt = tcpServer.get().getCompetences().iterator(); 
					while (compIt.hasNext()) {
						final Competence competence = compIt.next();
						if (competence.appliesTo(buffer)) {
							response = competence.processRequest(buffer);
							break;
						}
					}
					
					//byte[] response = ClientHandler.getBytesWithNewline(clientRequest);
					if (response != null) {
						for (final Action a : response) {
							if (a instanceof ResponseAction) {
								TestServerConsole.log("TCP/NTP Server (" + tcpServer.get().getServerSocket() 
										+ ") (:" + tcpServer.get().getPort() + "), response: " 
										+ new String(((ResponseAction) a).getData()) + " to: " + clientSocket.getInetAddress().toString(), 
										TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
							}
							a.execute(this, buffer, fos);
						}
					}
				}
				while (repeat.get());
			}
		}
		catch (SocketTimeoutException e) {
			TestServerConsole.error("TcpClientHandler Thread " + clientSocket.toString(), e, 2, TestServerServiceEnum.TCP_SERVICE);
		}
		catch (Exception e) {
			TestServerConsole.error("TcpClientHandler Thread " + clientSocket.toString(), e, 1, TestServerServiceEnum.TCP_SERVICE);
		}
		finally {
			try {
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch (Exception e) {
				TestServerConsole.error(name, e, 2, TestServerServiceEnum.TCP_SERVICE);
			}
		}
	}

	public AtomicBoolean getRepeat() {
		return repeat;
	}
}
