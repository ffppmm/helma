import static org.junit.Assert.*;

import java.io.IOException;

import helma.framework.core.Application;
import helma.main.Server;

import org.junit.Before;
import org.junit.Test;



public class ServerRunningTest {

	@Before
	public void createServer() {
		String[] args = new String[] {};
        Server server = Server.loadServer(args);
        // parse properties files etc
        try {
			server.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("could not init Server");
		}
        // start the server main thread
        server.start();	
	}
	
	@Test
	public void test() {
		Server srv = Server.getServer();
		assertNotNull("No Server found", srv);
		Application manageApp = null;
		try {
			manageApp = srv.getApplication("manage");
		} catch (Exception e) {
			fail("coud not get get manage app " + e);			
		}
		assertNotNull("manage app not found", manageApp);
	}

}
