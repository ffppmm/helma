import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import helma.framework.core.Application;
import helma.main.Server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerRunningTest {

	private Server srv = null;
	
	@Before
	public void createServer() {
		helma.util.JUnitHelper.startHelmaServer(null);
	}
	
	@Test
	public void test() {
		srv = Server.getServer();
		assertNotNull("No Server found", srv);
		Application manageApp = null;
		try {
			manageApp = srv.getApplication("manage");
		} catch (Exception e) {
			fail("coud not get get manage app " + e);			
		}
		assertNotNull("manage app not found", manageApp);
	}
	
	@After
	public void cleanUp() {
		helma.util.JUnitHelper.stopHelmaServer();
	}
}
