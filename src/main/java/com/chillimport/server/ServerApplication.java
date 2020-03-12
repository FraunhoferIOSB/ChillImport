package com.chillimport.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

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
      try {
        String httpHost = ((httpProxy.split(":"))[1]).substring(2);
        String httpPort = (httpProxy.split(":"))[2];

        System.out.println("http proxy: " + httpProxy);
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", httpHost);
        System.setProperty("http.proxyPort", httpPort);
      } catch (NullPointerException e) {
        System.out.println("Cannot resolve http proxy");
      }
    } else {
      System.out.println("No HTTP proxy defined.");
    }

    if (httpsProxy != null) {
      try {
        String httpsHost = ((httpsProxy.split(":"))[1]).substring(2);
        String httpsPort = (httpsProxy.split(":"))[2];

        System.out.println("https proxy: " + httpsProxy);
        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxyHost", httpsHost);
        System.setProperty("https.proxyPort", httpsPort);
      } catch (NullPointerException e) {
        System.out.println("Cannot resolve https proxy");
      }
    } else {
      System.out.println("No HTTP proxy defined.");
    }

    if (confvalue == null) {
      confvalue = DEFAULT_CONFIG_PATH;
      System.out.println("Config path was not specified. Using default:" +
                         DEFAULT_CONFIG_PATH);
    }
    FileManager.setPathsOnStartup(confvalue);
    String username = FileManager.readFromFile("username.cfg");

    if (!username.equals("")) {
      FileManager.setUsernameOnStartup(username);
      System.out.println("Found user name");
    }
    SpringApplication.run(ServerApplication.class, args);
  }
}
