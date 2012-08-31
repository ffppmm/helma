package helma.util;

import static org.junit.Assert.fail;
import helma.main.Server;

import java.io.IOException;

public class JUnitHelper {
	public static void startHelmaServer(String[] args) {
		if (args == null) {
			// use defaults
			args = new String[] {};
		}
		System.setProperty("helma.home", "target");
        Server server = Server.loadServer(args);
        // parse properties files etc
        try {
			server.init();
		} catch (IOException e) {
			e.printStackTrace();
			fail("could not init Server");
		}
        // start the server main thread
        server.start();	
	}
	
	public static void stopHelmaServer() {
		Server srv = Server.getServer();
		if (srv != null) {
			srv.shutdown();
		}
	}
}
