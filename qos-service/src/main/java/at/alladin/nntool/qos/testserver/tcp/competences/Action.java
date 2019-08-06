package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.OutputStream;

public interface Action {

	public boolean execute(final OutputStream os);
}
