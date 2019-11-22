package com.chillimport.server.controller;

import com.chillimport.server.FileManager;
import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.DataType;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConfigControllerTest {

    @Autowired
    private MockMvc mvc;
    
    private static String testpath;
    private static String sep = File.separator;
    
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	
    	
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	
    	TestSetup.setup();
    }
    

    @Test
    public void createAndSaveConfiguration() throws Exception {

        Configuration config = new Configuration(2,";", DataType.CSV);
        String compareString = Configuration.serialize(config);

        System.out.println(compareString);

        this.mvc.perform(post("/config/create").content(compareString)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    
    public void getConfiguration() throws Exception {

        this.mvc.perform(get("/config/single").param("configId", "25857854")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void getConfigurations() throws Exception {

        this.mvc.perform(get("/config/all")).andDo(print()).andExpect(status().isOk());
    }
}