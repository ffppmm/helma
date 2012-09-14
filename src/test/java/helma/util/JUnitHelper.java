package helma.util;

/*
 * #%L
 * HelmaObjectPublisher
 * %%
 * Copyright (C) 1998 - 2012 Helma Software
 * %%
 * Helma License Notice
 * 
 * The contents of this file are subject to the Helma License
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://adele.helma.org/download/helma/license.txt
 * #L%
 */

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
