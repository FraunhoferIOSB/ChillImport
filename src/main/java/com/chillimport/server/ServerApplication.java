package com.chillimport.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

  private static final Logger LOGGER              = LoggerFactory.getLogger(ServerApplication.class);
  private static final String DEFAULT_CONFIG_PATH = "/data";

  /**
   * @param args start up arguments
   */
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    String confvalue = System.getenv("configPath");
    String httpProxy = System.getenv("HTTP_PROXY");
    String httpsProxy = System.getenv("HTTPS_PROXY");

    if (httpProxy != null) {
        String httpHost = ((httpProxy.split(":"))[1]).substring(2);
        String httpPort = (httpProxy.split(":"))[2];

        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", httpHost);
        System.setProperty("http.proxyPort", httpPort);
        LOGGER.debug("Using http proxy: " + httpProxy);
    } else {
      LOGGER.debug("No http proxy specified.");
    }

    if (httpsProxy != null) {

        String httpsHost = ((httpsProxy.split(":"))[1]).substring(2);
        String httpsPort = (httpsProxy.split(":"))[2];

        System.out.println("https proxy: " + httpsProxy);
        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxyHost", httpsHost);
        System.setProperty("https.proxyPort", httpsPort);

        LOGGER.debug("Using https proxy: " + httpsProxy);

    } else {
    LOGGER.debug("No https proxy specified.");
    }

    if (confvalue == null) {
      confvalue = DEFAULT_CONFIG_PATH;
      LOGGER.info("Config path was not specified. Using default:" +
                         DEFAULT_CONFIG_PATH);
    }
    FileManager.setPathsOnStartup(confvalue);
    String username = FileManager.readFromFile("username.cfg");

    if (!username.equals("")) {
      FileManager.setUsernameOnStartup(username);
      LOGGER.info("Found user name");
    }
    SpringApplication.run(ServerApplication.class, args);
  }
}
