package com.chillimport.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ServerApplication {
	
	private static final String DEFAULT_CONFIG_PATH = "/data";

    /**
     * @param args start up arguments
     */
    public static void main(String[] args) {
        String confvalue = System.getenv("configPath");
        if (confvalue == null) {
        	confvalue = DEFAULT_CONFIG_PATH;
            System.out.println("Config path was not specified. Using default:" + DEFAULT_CONFIG_PATH);
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
