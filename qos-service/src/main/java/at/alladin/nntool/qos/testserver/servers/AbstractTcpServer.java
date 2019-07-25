/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nntool.qos.testserver.servers;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.entity.Observable;
import at.alladin.nntool.qos.testserver.entity.TestCandidate;
import at.alladin.nntool.qos.testserver.tcp.competences.BasicCompetence;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;

public abstract class AbstractTcpServer extends AbstractServer<ServerSocket, TestCandidate> implements Observable {

	
	/**
	 * counts the active connections
	 */
	protected final AtomicLong currentConnections = new AtomicLong(0); 
	
	/**
	 * the competences this server holds
	 */
	private final Deque<Competence> competences = new ArrayDeque<>();
	
	public AbstractTcpServer(InetAddress addr, int port) {
		super(ServerSocket.class, TestCandidate.class, addr, port, "TcpServer", TestServerServiceEnum.TCP_SERVICE);
		registerCompetence(new BasicCompetence());
	}
	
	public void registerCompetence(final Competence competence) {
		this.competences.addFirst(competence);
	}

	public Deque<Competence> getCompetences() {
		return competences;
	}
}
