// Server.java
// Copyright (c) Hannes Walln�fer 1998-2000
 
package helma.objectmodel.db;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Locale;
import helma.objectmodel.*;
import helma.framework.*;
import helma.framework.core.*;
import helma.xmlrpc.*;
import helma.util.*;
import com.sleepycat.db.*;


/**
 * HOP main class.
 */
 
 public class Server extends IServer implements Runnable {


    static boolean useTransactions, paranoid;
    
    private ApplicationManager appManager;

    private  Thread mainThread;

    static String dbFilename = "hop.db";
    static String propfile;
    static String dbPropfile = "db.properties";
    static String appsPropfile;
    static SystemProperties appsProps;
    static SystemProperties dbProps;
    static int port = 5055;
    static int webport = 0;

    Acme.Serve.Serve websrv;

    public static void main (String args[]) throws IOException {

	// check if we are running on a Java 2 VM - otherwise exit with an error message
	String jversion = System.getProperty ("java.version");
	if (jversion == null || jversion.startsWith ("1.1") || jversion.startsWith ("1.0")) {
	    System.err.println ("This version of Helma requires Java 1.2 or greater.");
	    if (jversion == null) // don't think this will ever happen, but you never know
	        System.err.println ("Your Java Runtime did not provide a version number. Please update to a more recent version.");
	    else
	        System.err.println ("Your Java Runtime is version "+jversion+". Please update to a more recent version.");
	    System.exit (1);	
	}
	
	String homeDir = null;

	boolean usageError = false;

	useTransactions = true;

	for (int i=0; i<args.length; i++) {
	    if (args[i].equals ("-h") && i+1<args.length)
	        homeDir = args[++i];
	    else if (args[i].equals ("-f") && i+1<args.length)
	        propfile = args[++i];
	    else if (args[i].equals ("-t"))
	        useTransactions = false;
	    else if (args[i].equals ("-p") && i+1<args.length) {
	        try {
	            port = Integer.parseInt (args[++i]);
	        } catch (Exception portx) {
	             usageError = true;
	        }
	    } else if (args[i].equals ("-w") && i+1<args.length) {
	        try {
	            webport = Integer.parseInt (args[++i]);
	        } catch (Exception portx) {
	             usageError = true;
	        }
	    } else
	        usageError = true;
	}

	if (usageError ) {
	    System.out.println ("usage: java helma.objectmodel.db.Server [-h dir] [-f file] [-p port] [-w port] [-t]");
	    System.out.println ("  -h dir     Specify hop home directory");
	    System.out.println ("  -f file    Specify server.properties file");
	    System.out.println ("  -p port    Specify TCP port number");
	    System.out.println ("  -w port    Start embedded Web server on that port");
	    System.out.println ("  -t         Disable Berkeley DB Transactions");
	    getLogger().log ("Usage Error - exiting");
	    System.exit (0);
	}

	new Server (homeDir);

    }

    public Server (String home) {

	String homeDir = home;

	// get main property file from home dir or vice versa, depending on what we have.
	// get property file from hopHome
	if (propfile == null) {
	    if (homeDir != null)
	        propfile = new File (homeDir, "server.properties").getAbsolutePath ();
	    else
	        propfile = new File ("server.properties").getAbsolutePath ();
	}

	sysProps = new SystemProperties (propfile);
	// get hopHome from property file
	if (homeDir == null)
	    homeDir = sysProps.getProperty ("hophome");
	if (homeDir == null)
	    homeDir =  new File (propfile).getParent ();

	// create hopHome File object
	hopHome = new File (homeDir);

	getLogger().log ("propfile = "+propfile);
	getLogger().log ("hopHome = "+hopHome);


	File helper = new File (hopHome, "db.properties");
	dbPropfile = helper.getAbsolutePath ();
	dbProps = new SystemProperties (dbPropfile);
	DbSource.setDefaultProps (dbProps);
	getLogger().log ("dbPropfile = "+dbPropfile);

	appsPropfile = sysProps.getProperty ("appsPropFile");
	if (appsPropfile != null && !"".equals (appsPropfile.trim()))
	    helper = new File (appsPropfile);
	else
	    helper = new File (hopHome, "apps.properties");
	appsPropfile = helper.getAbsolutePath ();
	getLogger().log ("appsPropfile = "+appsPropfile);

	paranoid = "true".equalsIgnoreCase (sysProps.getProperty ("paranoid"));

	String language = sysProps.getProperty ("language");
	String country = sysProps.getProperty ("country");
	String timezone = sysProps.getProperty ("timezone");

	if (language != null && country != null)
	    Locale.setDefault (new Locale (language, country));
	if (timezone != null)
	    TimeZone.setDefault (TimeZone.getTimeZone (timezone));

	getLogger().log ("Locale = "+Locale.getDefault());
	getLogger().log ("TimeZone = "+TimeZone.getDefault().getDisplayName (Locale.getDefault ()));

	dbSources = new Hashtable ();

	try {
	    checkRunning ();  // check if a server is already running with this db
	} catch (Exception running) {
	    System.out.println (running.getMessage ());
	    System.exit (1);
	}

	// nmgr = new NodeManager (this, sysProps);

	mainThread = new Thread (this);
	mainThread.start ();
    }

    public void run () {

	try {

	    // start embedded web server if port is specified
	    if (webport > 0) {
	        websrv = new Acme.Serve.Serve (webport, sysProps);
	    }

                 String xmlparser = sysProps.getProperty ("xmlparser");
	    if (xmlparser != null)
	        XmlRpc.setDriver (xmlparser);
	    // XmlRpc.setDebug (true);
	    xmlrpc = new WebServer (port+1);
	    if (paranoid) {
	        xmlrpc.setParanoid (true);
	        String xallow = sysProps.getProperty ("allowXmlRpc");
	        if (xallow != null) {
	            StringTokenizer st = new StringTokenizer (xallow, " ,;");
	            while (st.hasMoreTokens ())
	                xmlrpc.acceptClient (st.nextToken ());
	        }
	    }
	    getLogger().log ("Starting XML-RPC server on port "+(port+1));

	    // the following seems not to be necessary after all ...
	    // System.setSecurityManager(new RMISecurityManager());
	    if (paranoid) {
	        HopSocketFactory factory = new HopSocketFactory ();
	        String rallow = sysProps.getProperty ("allowWeb");
	        if (rallow != null) {
	            StringTokenizer st = new StringTokenizer (rallow, " ,;");
	            while (st.hasMoreTokens ())
	                factory.addAddress (st.nextToken ());
	        }
	        RMISocketFactory.setSocketFactory (factory);
	    }

	    if (websrv == null) {
	        getLogger().log ("Starting server on port "+port);
	        LocateRegistry.createRegistry (port);
	    }


	    // start application framework
	    appsProps = new SystemProperties (appsPropfile);
	    appManager = new ApplicationManager (port, hopHome, appsProps, this);


	} catch (Exception gx) {
	    getLogger().log ("Error initializing embedded database: "+gx);
	    gx.printStackTrace ();
	    return;
             }

	// start applications
	appManager.startAll ();

	// start embedded web server
	if (websrv != null) {
	    Thread webthread = new Thread (websrv, "WebServer");
	    webthread.start ();
	}

	int count = 0;
	while (Thread.currentThread () == mainThread) {
	    try {
	        mainThread.sleep (3000l);
	    } catch (InterruptedException ie) {}
	    try {
	        appManager.checkForChanges ();
	    } catch (Exception x) {
	        getLogger().log ("Caught in app manager loop: "+x);
	    }
	}

    }


    private void checkRunning () throws Exception {
	try {
	    java.net.Socket socket = new java.net.Socket ("localhost", port);
	} catch (Exception x) {
	    return;
	}
	// if we got so far, another server is already running on this port and db
	throw new Exception ("Error: Server already running on this port");
    }

}




































































