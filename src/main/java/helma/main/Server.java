package helma.main;

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

import helma.extensions.HelmaExtension;
import helma.framework.core.Application;
import helma.framework.repository.FileResource;
import helma.orm.db.DbSource;
import helma.util.Logging;
import helma.util.ResourceProperties;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;

/**
 * Helma server main class.
 */
public class Server implements Runnable {
	// version string
	public static final String version = "1.8.0";

	// static server instance
	private static Server server;

	// Server home directory
	protected File hopHome;

	// server-wide properties
	ResourceProperties appsProps;
	ResourceProperties dbProps;
	ResourceProperties sysProps;

	// our logger
	private Log logger;
	// are we using helma.util.Logging?
	private boolean helmaLogging;

	// server start time
	public final long starttime;

	// if paranoid == true we only accept XML-RPC connections from
	// explicitly listed hosts.
	public boolean paranoid;
	private ApplicationManager appManager;
	private Vector<HelmaExtension> extensions;
	private Thread mainThread;

	// configuration
	ServerConfig config;

	// the embedded web server
	// protected Serve websrv;
	protected JettyServer jetty;

	// the XML-RPC server
	protected WebServer xmlrpc;

	Thread shutdownhook;

	/**
	 * Constructs a new Server instance with an array of command line options.
	 * TODO make this a singleton
	 * 
	 * @param config
	 *            the configuration
	 */
	public Server(ServerConfig config) {
		server = this;
		starttime = System.currentTimeMillis();

		this.config = config;
		hopHome = config.getHomeDir();
		if (hopHome == null) {
			throw new RuntimeException("helma.home property not set");
		}

		// create system properties
		sysProps = new ResourceProperties();
		if (config.hasPropFile()) {
			sysProps.addResource(new FileResource(config.getPropFile()));
		}
	}

	/**
	 * Static main entry point.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		loadServer(args);
		// parse properties files etc
		server.init();
		// start the server main thread
		server.start();
	}

	/**
	 * Entry point used by launcher.jar to load a server instance
	 * 
	 * @param args
	 *            the command line arguments
	 * @return the server instance
	 */
	public static Server loadServer(String[] args) {
		ServerConfig config;
		try {
			config = ServerConfig.getInstance();
			checkRunning(config);
			// create new server instance
			server = new Server(config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return server;
	}

	/**
	 * print the usage hints and prefix them with a message.
	 */
	public static void printUsageError(String msg) {
		System.out.println(msg);
		printUsageError();
	}

	/**
	 * print the usage hints
	 */
	public static void printUsageError() {
		System.out.println("");
		System.out.println("Usage: java helma.main.Server [options]");
		System.out.println("Possible options:");
		System.out.println("  -a app[,...]      Specify applications to start");
		System.out.println("  -h dir            Specify hop home directory");
		System.out
				.println("  -f file           Specify server.properties file");
		System.out
				.println("  -c jetty.xml      Specify Jetty XML configuration file");
		System.out
				.println("  -w [ip:]port      Specify embedded web server address/port");
		System.out.println("  -x [ip:]port      Specify XML-RPC address/port");
		System.out.println("  -jk [ip:]port     Specify AJP13 address/port");
		System.out.println("");
		System.out.println("Supported formats for server ports:");
		System.out.println("   <port-number>");
		System.out.println("   <ip-address>:<port-number>");
		System.out.println("   <hostname>:<port-number>");
		System.out.println("");
		System.err.println("Usage Error - exiting");
		System.out.println("");
	}

	/**
	 * Check wheter a server is already running on any of the given ports -
	 * otherwise exit with an error message
	 */
	public static void checkRunning(ServerConfig config) {
		// check if any of the specified server ports is in use already
		try {
			if (config.hasWebsrvPort()) {
				checkPort(config.getWebsrvPort());
			}

			if (config.hasXmlrpcPort()) {
				checkPort(config.getXmlrpcPort());
			}

			if (config.hasAjp13Port()) {
				checkPort(config.getAjp13Port());
			}
		} catch (Exception running) {
			System.out.println(running.getMessage());
			System.exit(1);
		}

	}

	/**
	 * Check whether a server port is available by trying to open a server
	 * socket
	 */
	private static void checkPort(InetSocketAddress endpoint)
			throws IOException {
		try {
			ServerSocket sock = new ServerSocket();
			sock.bind(endpoint);
			sock.close();
		} catch (IOException x) {
			throw new IOException("Error binding to " + endpoint + ": "
					+ x.getMessage());
		}
	}

	/**
	 * initialize the server
	 */
	public void init() throws IOException {

		// set the log factory property
		String logFactory = sysProps.getProperty("loggerFactory",
				"helma.util.Logging");

		helmaLogging = "helma.util.Logging".equals(logFactory);
		System.setProperty("org.apache.commons.logging.LogFactory", logFactory);

		// set the current working directory to the helma home dir.
		// note that this is not a real cwd, which is not supported
		// by java. It makes sure relative to absolute path name
		// conversion is done right, so for Helma code, this should work.
		System.setProperty("user.dir", hopHome.getPath());

		// from now on it's safe to call getLogger() because hopHome is set up
		getLogger();

		String startMessage = "Starting Helma " + version + " on Java "
				+ System.getProperty("java.version");

		logger.info(startMessage);

		// also print a msg to System.out
		System.out.println(startMessage);

		logger.info("Setting Helma Home to " + hopHome);

		// read db.properties file in helma home directory
		String dbPropfile = sysProps.getProperty("dbPropFile");
		File file;
		if ((dbPropfile != null) && !"".equals(dbPropfile.trim())) {
			file = new File(dbPropfile);
		} else {
			file = new File(hopHome, "db.properties");
		}

		dbProps = new ResourceProperties();
		dbProps.setIgnoreCase(false);
		dbProps.addResource(new FileResource(file));
		DbSource.setDefaultProps(dbProps);

		// read apps.properties file
		String appsPropfile = sysProps.getProperty("appsPropFile");
		if ((appsPropfile != null) && !"".equals(appsPropfile.trim())) {
			file = new File(appsPropfile);
		} else {
			file = new File(hopHome, "apps.properties");
		}
		appsProps = new ResourceProperties();
		appsProps.setIgnoreCase(true);
		appsProps.addResource(new FileResource(file));

		paranoid = "true".equalsIgnoreCase(sysProps.getProperty("paranoid"));

		String language = sysProps.getProperty("language");
		String country = sysProps.getProperty("country");
		String timezone = sysProps.getProperty("timezone");

		if ((language != null) && (country != null)) {
			Locale.setDefault(new Locale(language, country));
		}

		if (timezone != null) {
			TimeZone.setDefault(TimeZone.getTimeZone(timezone));
		}

		// logger.debug("Locale = " + Locale.getDefault());
		// logger.debug("TimeZone = " +
		// TimeZone.getDefault().getDisplayName(Locale.getDefault()));

		// try to load the extensions
		extensions = new Vector<HelmaExtension>();
		if (sysProps.getProperty("extensions") != null) {
			initExtensions();
		}
		jetty = JettyServer.init(this, config);
	}

	/**
	 * initialize extensions
	 */
	private void initExtensions() {
		StringTokenizer tok = new StringTokenizer(
				sysProps.getProperty("extensions"), ",");
		while (tok.hasMoreTokens()) {
			String extClassName = tok.nextToken().trim();

			try {
				Class<?> extClass = Class.forName(extClassName);
				HelmaExtension ext = (HelmaExtension) extClass.newInstance();
				ext.init(this);
				extensions.add(ext);
				logger.info("Loaded: " + extClassName);
			} catch (Throwable e) {
				logger.error("Error loading extension " + extClassName + ": "
						+ e.toString());
			}
		}
	}

	public void start() {
		// Start running, finishing setup and then entering a loop to check
		// changes
		// in the apps.properties file.
		mainThread = new Thread(this);
		mainThread.start();
	}

	public void stop() {
		mainThread = null;
		appManager.stopAll();
	}

	public void shutdown() {
		getLogger().info("Shutting down Helma");

		if (jetty != null) {
			try {
				jetty.stop();
				jetty.destroy();
			} catch (Exception x) {
				// exception in jettx stop. ignore.
			}
		}

		if (xmlrpc != null) {
			try {
				xmlrpc.shutdown();
			} catch (Exception x) {
				// exception in xmlrpc server shutdown, ignore.
			}
		}

		if (helmaLogging) {
			Logging.shutdown();
		}

		server = null;

		try {
			Runtime.getRuntime().removeShutdownHook(shutdownhook);
			// HACK: running the shutdownhook seems to be necessary in order
			// to prevent it from blocking garbage collection of helma
			// classes/classloaders. Since we already set server to null it
			// won't do anything anyhow.
			shutdownhook.start();
			shutdownhook = null;
		} catch (Exception x) {
			// invalid shutdown hook or already shutting down. ignore.
		}
	}

	/**
	 * The main method of the Server. Basically, we set up Applications and than
	 * periodically check for changes in the apps.properties file, shutting down
	 * apps or starting new ones.
	 */
	public void run() {
		try {
			if (config.hasXmlrpcPort()) {
				InetSocketAddress xmlrpcPort = config.getXmlrpcPort();
				String xmlparser = sysProps.getProperty("xmlparser");

				if (xmlparser != null) {
					XmlRpc.setDriver(xmlparser);
				}

				if (xmlrpcPort.getAddress() != null) {
					xmlrpc = new WebServer(xmlrpcPort.getPort(),
							xmlrpcPort.getAddress());
				} else {
					xmlrpc = new WebServer(xmlrpcPort.getPort());
				}

				if (paranoid) {
					xmlrpc.setParanoid(true);

					String xallow = sysProps.getProperty("allowXmlRpc");

					if (xallow != null) {
						StringTokenizer st = new StringTokenizer(xallow, " ,;");

						while (st.hasMoreTokens())
							xmlrpc.acceptClient(st.nextToken());
					}
				}
				xmlrpc.start();
				logger.info("Starting XML-RPC server on port " + (xmlrpcPort));
			}

			appManager = new ApplicationManager(appsProps, this);

			if (xmlrpc != null) {
				xmlrpc.addHandler("$default", appManager);
			}

			// add shutdown hook to close running apps and servers on exit
			shutdownhook = new HelmaShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownhook);
		} catch (Exception x) {
			throw new RuntimeException("Error setting up Server", x);
		}

		// set the security manager.
		// the default implementation is helma.main.HelmaSecurityManager.
		try {
			String secManClass = sysProps.getProperty("securityManager");

			if (secManClass != null) {
				SecurityManager secMan = (SecurityManager) Class.forName(
						secManClass).newInstance();

				System.setSecurityManager(secMan);
				logger.info("Setting security manager to " + secManClass);
			}
		} catch (Exception x) {
			logger.error("Error setting security manager", x);
		}

		// start applications
		appManager.startAll();

		// start embedded web server
		if (jetty != null) {
			try {
				jetty.start();
			} catch (Exception m) {
				throw new RuntimeException(
						"Error starting embedded web server", m);
			}
		}

		while (Thread.currentThread() == mainThread) {
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException ie) {
			}

			try {
				appManager.checkForChanges();
			} catch (Exception x) {
				logger.warn("Caught in app manager loop: " + x);
			}
		}
	}

	/**
	 * Make sure this server has an ApplicationManager (e.g. used when accessed
	 * from CommandlineRunner)
	 */
	public void checkAppManager() {
		if (appManager == null) {
			appManager = new ApplicationManager(appsProps, this);
		}
	}

	/**
	 * Get an Iterator over the applications currently running on this Server.
	 */
	public Object[] getApplications() {
		return appManager.getApplications();
	}

	/**
	 * Get an Application by name
	 */
	public Application getApplication(String name) {
		return appManager.getApplication(name);
	}

	/**
	 * Get a logger to use for output in this server.
	 */
	public Log getLogger() {
		if (logger == null) {
			if (helmaLogging) {
				// set up system properties for helma.util.Logging
				String logDir = sysProps.getProperty("logdir", "log");

				if (!"console".equals(logDir)) {
					// try to get the absolute logdir path

					// set up helma.logdir system property
					File dir = new File(logDir);
					if (!dir.isAbsolute()) {
						dir = new File(hopHome, logDir);
					}

					logDir = dir.getAbsolutePath();
				}
				System.setProperty("helma.logdir", logDir);
			}
			logger = LogFactory.getLog("helma.server");
		}

		return logger;
	}

	/**
	 * Get the Home directory of this server.
	 */
	public File getHopHome() {
		return hopHome;
	}

	/**
	 * Get the explicit list of apps if started with -a option
	 * 
	 * @return
	 */
	public String[] getApplicationsOption() {
		return config.getApps();
	}

	/**
	 * Get the main Server instance.
	 */
	public static Server getServer() {
		return server;
	}

	/**
	 * Get the Server's XML-RPC web server.
	 */
	public static WebServer getXmlRpcServer() {
		return server.xmlrpc;
	}

	/**
	 * 
	 * 
	 * @param key
	 *            ...
	 * 
	 * @return ...
	 */
	public String getProperty(String key) {
		return (String) sysProps.get(key);
	}

	/**
	 * Return the server.properties for this server
	 * 
	 * @return the server.properties
	 */
	public ResourceProperties getProperties() {
		return sysProps;
	}

	/**
	 * Return the server-wide db.properties
	 * 
	 * @return the server-wide db.properties
	 */
	public ResourceProperties getDbProperties() {
		return dbProps;
	}

	/**
	 * Return the apps.properties entries for a given application
	 * 
	 * @param appName
	 *            the app name
	 * @return the apps.properties subproperties for the given app
	 */
	public ResourceProperties getAppsProperties(String appName) {
		if (appName == null) {
			return appsProps;
		} else {
			return appsProps.getSubProperties(appName + ".");
		}
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public File getAppsHome() {
		String appHome = sysProps.getProperty("appHome", "");

		if (appHome.trim().length() != 0) {
			return new File(appHome);
		} else {
			return new File(hopHome, "apps");
		}
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public File getDbHome() {
		String dbHome = sysProps.getProperty("dbHome", "");

		if (dbHome.trim().length() != 0) {
			return new File(dbHome);
		} else {
			return new File(hopHome, "db");
		}
	}

	/**
	 * 
	 * 
	 * @return ...
	 */
	public Vector<HelmaExtension> getExtensions() {
		return extensions;
	}

	/**
	 * 
	 * 
	 * @param name
	 *            ...
	 */
	public void startApplication(String name) {
		appManager.start(name);
		appManager.register(name);
	}

	/**
	 * 
	 * 
	 * @param name
	 *            ...
	 */
	public void stopApplication(String name) {
		appManager.stop(name);
	}
}
