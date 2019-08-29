package at.alladin.nntool.qos.testserver;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpTestTest {

	public static void main(String[] args) {
		try (final Socket s = new Socket("127.0.0.1", 3000);
				final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))
				) {
			
			bw.write("Test 1\nTest 2\nTest 3\n");
			bw.flush();
			Thread.sleep(1000);
			bw.write("Test 2\n");
			bw.flush();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
