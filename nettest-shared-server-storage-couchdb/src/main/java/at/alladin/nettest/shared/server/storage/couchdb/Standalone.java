package at.alladin.nettest.shared.server.storage.couchdb;

import at.alladin.nettest.shared.server.storage.couchdb.Standalone.InitDbCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "couchdb.Standalone",
	subcommands = { InitDbCommand.class }
)
public class Standalone implements Runnable {

	@Option(names = {"-u", "--url"}, description = "CouchDB URL (without /database-name)", required = true)
	private String url;
	
	@Option(names = {"-U", "--username"}, description = "CouchDB username")
	private String username;
	
	@Option(names = {"-P", "--password"}, description = "CouchDB password")
	private String password;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("TEST");
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
	}
	
	public static void main(String[] args) {
		CommandLine.run(new Standalone(), args);
	}
	
	@Command(name = "initDb")
	static class InitDbCommand {
		
	}
}

