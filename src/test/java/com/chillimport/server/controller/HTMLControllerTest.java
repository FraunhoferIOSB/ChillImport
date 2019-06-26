package com.chillimport.server.controller;

import com.chillimport.server.FileManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HTMLControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void websitePreview() throws Exception {

        String content = "https://raw.githubusercontent.com/uzkns/beispielcsv/master/Messergebnisse.xlsx";

        MvcResult result = this.mvc.perform(post("/websitepreview").param("s", content)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[[\"Date and time\",\"Location\",\"Sensor\",\"Temperature\",\"Wind speed\",\"Weather\",\"null\"],[\"10.02.2017 12:00:00\",\"Meckenheimer Allee 171, 53115 Bonn\",\"TmpAndSpd-1\",\"5.0\",\"21.0\",\"sunny\",\"null\"],[\"10.02.2017 14:00:00\",\"Meckenheimer Allee 171, 53115 Bonn\",\"TmpAndSpd-1\",\"7.0\",\"21.0\",\"sunny\",\"null\"]]");
    }


    @Test
    public void returnFile() throws Exception {

        MvcResult result = this.mvc.perform(get("/errors/returnFiles")).andDo(print()).andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        String[] resp = response.substring(1, response.length() - 1).split(",");
        for (String s : resp) {
            s = s.substring(1, s.length() - 1);
            s = FileManager.getLogPath() + File.separator + "returnRows" + File.separator + s;
            File file = new File(s);
            Assert.assertEquals(file.exists(), true);
        }

    }

    @Test
    public void pingFROSTServer() throws Exception {

        MvcResult result = this.mvc.perform(get("/server-check")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("true")).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(), "true");
    }

    @Test
    public void downloadFile() throws Exception {

        MvcResult result = this.mvc.perform(get("/get-return").param("fileName", "testfile").accept("application/octet-stream"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(), "test datei");
    }


    @Test
    public void getFrostServerURL() throws Exception {

        MvcResult result = this.mvc.perform(get("/getfrosturl")).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(), "https://pse-frost.cluster.pilleslife.de/v1.0");
    }

    @Test
    public void getConfigurations() throws Exception {

        this.mvc.perform(get("/config/all")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void delFileInvalid() throws Exception {

        MvcResult result = this.mvc.perform(get("/errors/delFile").param("name",
                                                                         "invalidname")).andDo(print()).andExpect(status().isInternalServerError()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(), "Could not delete File");
    }
}