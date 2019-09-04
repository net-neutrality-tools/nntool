package at.alladin.nntool.qos.testserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TestTcp {

	public static void main(String[] args) throws IOException {
		try(final ServerSocket serverSocket = new ServerSocket(3000)) {
			System.out.println("Listening on TCP 3000...");
			while (true) {
				final Socket s = serverSocket.accept();
				//new TcpHandler(s).runDis();
				new TcpHandler(s).runBr();
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static class TcpHandler {
		
		final Socket socket;
		
		public TcpHandler(final Socket socket) {
			this.socket = socket;
		}
		
		public void runBr() {
			try (final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				long nulls = 0;
				while (true) {
					final String line = br.readLine();
					if (line == null) {
						nulls++;
					}
					else if (line != null) {
						System.out.println("TS: " + System.currentTimeMillis() + ", LINE: " + line + " (NULLS: " + nulls + ")");
					}
				}
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
		
		public void runDis() {
			try (final DataInputStream dis = new DataInputStream(socket.getInputStream())) {
				long nulls = 0;
				while (true) {
					final byte[] buffer = new byte[1024];
					if (dis.read(buffer) > -1) {
						System.out.println("TS: " + System.currentTimeMillis() + ", LINE: " + new String(buffer) + " (NULLS: " + nulls + ")");						
					}
				}
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}
}