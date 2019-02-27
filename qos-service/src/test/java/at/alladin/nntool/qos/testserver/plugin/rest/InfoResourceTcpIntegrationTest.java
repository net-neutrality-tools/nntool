package at.alladin.nntool.qos.testserver.plugin.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import at.alladin.nntool.qos.testserver.ClientHandler;
import at.alladin.nntool.qos.testserver.ServerPreferences;
import at.alladin.nntool.qos.testserver.TestServer;
import at.alladin.nntool.qos.testserver.tcp.TcpMultiClientServer;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class InfoResourceTcpIntegrationTest {

	InfoResource ir;
	
	@Mocked ServerSocket serverSocket;
	
	@Mocked DatagramChannel datagramChannel;
	
	@Mocked DatagramSocket datagramSocket;
	
	@Mocked DatagramPacket datagramPacket;
	
	@Mocked Socket socket;
	
	@Mocked ExecutorService executorService;
	
	@Mocked ClientHandler clientHandler;
	
	//@Mocked TestServerConsole console;
	
	@Before
	public void init() throws Exception {
		TestServer./*get*/newInstance().run(new ServerPreferences(getClass().getResourceAsStream("config_udp.properties")));
		ir = new InfoResource();
		
		new MockUp<RestService>() {
			
			@Mock
			public void start() throws UnknownHostException {
				//do nothing;
			}
		};
	}
	
	@After
	public void teardown() {
		TestServer.getInstance().shutdown();
		//TestServer.newInstance();
	}

	//@Test
	public void testRequestTcp() throws Exception {		
		new MockUp<ServerResource>() {
			@Mock
			public String getAttribute(String name) {
				return "tcp";
			}
		};
		
		final TcpMultiClientServer s = new TcpMultiClientServer(1000, InetAddress.getByName("1.2.3.4"));
		s.refreshTtl(4321);
		s.setServerSocket(serverSocket);
		final List<TcpMultiClientServer> tcpList = new ArrayList<>();
		tcpList.add(s);
		TestServer.getInstance().tcpServerMap.put(1000, tcpList);
		
		final JsonObject json = (JsonObject) new Gson().fromJson(ir.request(), JsonObject.class);
		assertNotNull(json);
		assertEquals("'protocol_type' != 'tcp'", "tcp", json.get("protocol_type").getAsString());
		assertEquals("Amount of TCP servers != 1", 1, json.get("servers").getAsJsonArray().size());
		assertEquals("TCP server [0] port != 1000", 1000, json.get("servers").getAsJsonArray().get(0).getAsJsonObject().get("port").getAsInt());
	}
	
	//@Test
	public void testRequestUnknownProtocol() throws Exception {		
		new MockUp<ServerResource>() {
			@Mock
			public String getAttribute(String name) {
				return "unknown_protocol_xyz";
			}
		};
		
		final JsonObject json = (JsonObject) new Gson().fromJson(ir.request(), JsonObject.class);
		assertNotNull(json);
		assertEquals("'protocol_type' != 'unknown'", "unknown", json.get("protocol_type").getAsString());
	}

}
