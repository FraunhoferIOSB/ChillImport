package com.chillimport.server.controller;

import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.ConfigurationManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void uploadFile() throws Exception {
        byte[] fileContent = "bar".getBytes(StandardCharsets.UTF_8);
        MockPart filePart = new MockPart("file", "orig.csv", fileContent);

        byte[] json = "{\"name\":\"orig2.csv\"}".getBytes(StandardCharsets.UTF_8);
        MockPart jsonPart = new MockPart("json", "json", json);
        jsonPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.multipart("/upload").part(filePart).part(jsonPart))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString().endsWith(".csv"), true);
    }

    @Ignore //no content
    @Test
    public void uploadFileFromWebsite() throws Exception {

        String content = "https://raw.githubusercontent.com/uzkns/beispielcsv/master/Messergebnisse.xlsx";

        MvcResult result = this.mvc.perform(post("/uploadFromUrl").param("url", content))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString().endsWith(".xlsx"), true);

    }

    @Ignore //no content
    @Test
    public void importData() throws Exception {

        //Dateien vorbereiten
        String content = "https://raw.githubusercontent.com/uzkns/beispielcsv/master/Messergebnisse.xlsx";

        MvcResult fileresult = this.mvc.perform(post("/uploadFromUrl").param("url", content))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String newfilename = fileresult.getResponse().getContentAsString();

        //Config vorbereiten
        Configuration testConfig = ConfigurationManager.loadConfig(1011009511);
        String config = Configuration.serialize(testConfig);


        MvcResult result = this.mvc.perform(post("/importQueue").param("config", config).param("filename", newfilename))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString().startsWith("Finished import of file "), true);
    }

    @Test
    public void getProgress() throws Exception {

        this.mvc.perform(get("/progress")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("Import has not started yet"));

    }
}