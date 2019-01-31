package at.alladin.nntool.qos.testserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import at.alladin.nntool.qos.testserver.entity.ClientToken;
import at.alladin.nntool.qos.testserver.mock.RecordingFilterOutputStreamMock;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;
import mockit.Mocked;

/**
 * 	
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class ClientHandlerTest {

	@Mocked Socket socket;
	
	@Mocked ServerSocket serverSocket;
	
	@Mocked TestServerConsole console;
	
	@Test
	public void testSendMessage() throws IOException {
		final RecordingFilterOutputStreamMock ros = new RecordingFilterOutputStreamMock();		
		final ClientHandler ch = new ClientHandler(serverSocket, socket);
		
		ch.sendCommand("TEST");
		assertEquals("command received by client != 'TEST\\n'", "TEST\n", ros.getContent());
		
		ch.sendCommand("TEST", null);
		assertEquals("command received by client != 'TEST\\n'", "TEST\n", ros.getContent());
		
		ch.sendCommand("TEST", "TEST REQUEST");
		assertEquals("command received by client != 'TEST\\n'", "TEST\n", ros.getContent());
	}
	
	@Test
	public void testSendMessageContainingIDAppendix() throws IOException {
		final RecordingFilterOutputStreamMock ros = new RecordingFilterOutputStreamMock();		
		final ClientHandler ch = new ClientHandler(serverSocket, socket);
		
		ch.sendCommand("TEST", "TEST +ID0");		
		assertEquals("command received by client != 'TEST +ID0\\n'", "TEST +ID0\n", ros.getContent());
	}
	
	@Test(expected=IOException.class)
	public void testCheckTokenWithBadToken() throws IOException {
		final RecordingFilterOutputStreamMock ros = new RecordingFilterOutputStreamMock();		
		final ClientHandler ch = new ClientHandler(serverSocket, socket);
		ch.checkToken("TOKEN 1234");
	}
	
	@Test
	public void testCheckTokenWithValidToken() throws NoSuchFieldException, SecurityException, Exception {
		final RecordingFilterOutputStreamMock ros = new RecordingFilterOutputStreamMock();
		final ClientHandler ch = new ClientHandler(serverSocket, socket);
		final ClientToken token = 
				ch.checkToken("TOKEN bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=\n");
		
		assertNotNull("Token is null", token);
		assertEquals("token timestamp != 1528136454", 1528136454, token.getTimeStamp());
		assertEquals("token UUID != 'bbd1ee96-0779-4619-b993-bb4bf7089754'", 
				"bbd1ee96-0779-4619-b993-bb4bf7089754", token.getUuid());
		assertEquals("token Hmac != '3gr2gw9lVhtVONV0XO62Vamu/uw='", 
				"3gr2gw9lVhtVONV0XO62Vamu/uw=", token.getHmac());		
	}	
}
