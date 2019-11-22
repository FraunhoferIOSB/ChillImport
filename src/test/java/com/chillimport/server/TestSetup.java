package com.chillimport.server;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TestSetup {
	private static boolean done = false;
	private static String testpath;
    private static String sep = File.separator;
	
	public static void setup() throws Exception {
		if (!done) {
			
			System.out.println("TestSetup started");
			testpath = "src" + sep + "test" + sep + "TempFolder";
			
			File dir = new File(testpath);
			//0: clear folder
			if(dir.exists()) {
				FileUtils.forceDelete(dir);
			}
			
			//1: create folder
			if(!dir.mkdir()) {
				throw new Exception("Error at setup");
			}
			
			dir.deleteOnExit();
			
			//2: fill folder
			
			File source = new File("src" + sep + "test" + sep + "resources");
			FileUtils.copyDirectoryToDirectory(source, dir);
			
			//3: set path
			FileManager.setPathsOnStartup(testpath + sep + "resources");
	    	
	    	
	    	//4: set shutdown hook
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				if(dir.exists()) {
					try {
						FileUtils.forceDelete(dir);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			
			
			done = true;
		}
	}
}
