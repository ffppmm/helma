/*
 * Helma License Notice
 *
 * The contents of this file are subject to the Helma License
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://adele.helma.org/download/helma/license.txt
 *
 * Copyright 1998-2003 Helma Software. All Rights Reserved.
 *
 * $RCSfile$
 * $Author$
 * $Revision$
 * $Date$
 */

package helma.main;

import helma.framework.repository.FileResource;
import helma.util.ResourceProperties;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Utility class for server config
 */
 
public class ServerConfig {

    private InetSocketAddress xmlrpcPort = null;
    private InetSocketAddress websrvPort = null;
    private InetSocketAddress ajp13Port  = null;
    private File propFile   = null;
    private File homeDir    = null;
    private File configFile = null;
    private String[] apps = null;
    
    private volatile static ServerConfig uniqueInstance = null;

    private ServerConfig() {
    	
    }

    public static ServerConfig getInstance() throws Exception {
        if (uniqueInstance == null) {
        	synchronized (ServerConfig.class) {
            	uniqueInstance = new ServerConfig();				
			}
	        // get possible environment setting for helma home
	        if (System.getProperty("helma.home") != null) {
	        	uniqueInstance.setHomeDir(new File(System.getProperty("helma.home")));
	        }
	
	        guessConfig(uniqueInstance);
	
	        // create system properties
	        ResourceProperties sysProps = new ResourceProperties();
	        sysProps.addResource(new FileResource(uniqueInstance.getPropFile()));
	
	        // check if there's a property setting for those ports not specified via command line
	        try {
		        if (!uniqueInstance.hasWebsrvPort() && sysProps.getProperty("webPort") != null) {
		        	uniqueInstance.setWebsrvPort(getInetSocketAddress(sysProps.getProperty("webPort")));
		        } else if (System.getProperty("helma.httpport") != null) {
		        	uniqueInstance.setWebsrvPort(getInetSocketAddress(System.getProperty("helma.httpport")));
		        }
	        } catch (Exception portx) {
	            throw new Exception("Error parsing web server port property from server.properties: " + portx);
	        }
	
	        if (!uniqueInstance.hasAjp13Port() && sysProps.getProperty("ajp13Port") != null) {
	            try {
	            	uniqueInstance.setAjp13Port(getInetSocketAddress(sysProps.getProperty("ajp13Port")));
	            } catch (Exception portx) {
	                throw new Exception("Error parsing AJP1.3 server port property from server.properties: " + portx);
	            }
	        }
	
	        if (!uniqueInstance.hasXmlrpcPort() && sysProps.getProperty("xmlrpcPort") != null) {
	            try {
	            	uniqueInstance.setXmlrpcPort(getInetSocketAddress(sysProps.getProperty("xmlrpcPort")));
	            } catch (Exception portx) {
	                throw new Exception("Error parsing XML-RPC server port property from server.properties: " + portx);
	            }
	        }
        }
        return uniqueInstance;
	}

	public boolean hasPropFile() {
        return (propFile != null);
    }

    public boolean hasConfigFile() {
        return (configFile != null);
    }

    public boolean hasHomeDir() {
        return (homeDir != null);
    }

    public boolean hasXmlrpcPort() {
        return (xmlrpcPort != null);
    }

    public boolean hasWebsrvPort() {
        return (websrvPort != null);
    }

    public boolean hasAjp13Port() {
        return (ajp13Port != null);
    }

    public boolean hasApps() {
        return (apps != null);
    }

    public InetSocketAddress getXmlrpcPort() {
        return xmlrpcPort;
    }

    public void setXmlrpcPort(InetSocketAddress xmlrpcPort) {
        this.xmlrpcPort = xmlrpcPort;
    }

    public InetSocketAddress getWebsrvPort() {
        return websrvPort;
    }

    public void setWebsrvPort(InetSocketAddress websrvPort) {
        this.websrvPort = websrvPort;
    }

    public InetSocketAddress getAjp13Port() {
        return ajp13Port;
    }

    public void setAjp13Port(InetSocketAddress ajp13Port) {
        this.ajp13Port = ajp13Port;
    }

    public File getPropFile() {
        return propFile;
    }

    public void setPropFile(File propFile) {
        this.propFile = propFile == null ? null : propFile.getAbsoluteFile();
    }

    public File getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(File homeDir) {
        this.homeDir = homeDir == null ? null : homeDir.getAbsoluteFile();
    }
    
    public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile == null ? null : configFile.getAbsoluteFile();
	}

    public String[] getApps() {
        return apps;
    }

    public void setApps(String[] apps) {
        this.apps = apps;
    }
    
    /**
     * check if we are running on a Java 2 VM - otherwise exit with an error message
     */
   public static void checkJavaVersion() {
       String javaVersion = System.getProperty("java.version");

       if ((javaVersion == null) || javaVersion.startsWith("1.4")
				                  || javaVersion.startsWith("1.3")
                                 || javaVersion.startsWith("1.2")
                                 || javaVersion.startsWith("1.1")
                                 || javaVersion.startsWith("1.0")) {
           System.err.println("This version of Helma requires Java 1.5 or greater.");

           if (javaVersion == null) { // don't think this will ever happen, but you never know
               System.err.println("Your Java Runtime did not provide a version number. Please update to a more recent version.");
           } else {
               System.err.println("Your Java Runtime is version " + javaVersion +
                                  ". Please update to a more recent version.");
           }

           System.exit(1);
       }
   }
   /**
    * get main property file from home dir or vice versa,
    * depending on what we have
    */
  public static void guessConfig(ServerConfig config) throws Exception {
      // get property file from hopHome:
      if (!config.hasPropFile()) {
          if (config.hasHomeDir()) {
              config.setPropFile(new File(config.getHomeDir(), "server.properties"));
          } else {
              config.setPropFile(new File("server.properties"));
          }
      }

      // create system properties
      ResourceProperties sysProps = new ResourceProperties();
      sysProps.addResource(new FileResource(config.getPropFile()));

      // try to get hopHome from property file
      if (!config.hasHomeDir() && sysProps.getProperty("hophome") != null) {
          config.setHomeDir(new File(sysProps.getProperty("hophome")));
      }

      // use the directory where server.properties is located:
      if (!config.hasHomeDir() && config.hasPropFile()) {
          config.setHomeDir(config.getPropFile().getAbsoluteFile().getParentFile());
      }

      if (!config.hasPropFile()) {
          throw new Exception ("no server.properties found");
      }

      if (!config.hasHomeDir()) {
          throw new Exception ("couldn't determine helma directory");
      }
  }
  
  private static InetSocketAddress getInetSocketAddress(String inetAddrPort)
          throws UnknownHostException {
      InetAddress addr = null;
      int c = inetAddrPort.indexOf(':');
      if (c >= 0) {
          String a = inetAddrPort.substring(0, c);
          if (a.indexOf('/') > 0)
              a = a.substring(a.indexOf('/') + 1);
          inetAddrPort = inetAddrPort.substring(c + 1);

          if (a.length() > 0 && !"0.0.0.0".equals(a)) {
              addr = InetAddress.getByName(a);
          }
      }
      int port = Integer.parseInt(inetAddrPort);
      return new InetSocketAddress(addr, port);
  }
}
