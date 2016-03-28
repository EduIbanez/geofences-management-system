package es.unizar.iaaa.geofencing.web;

import com.google.gson.Gson;
import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void createUser() throws Exception {
        User usuario = new User(1, "example.gmail.com", "password", "First", "Last", "07/08/1992", "356938035643809", null);
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        this.mockMvc.perform(post("/api/users").contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("example.gmail.com"))
                .andExpect(jsonPath("$.pass").value("password"))
                .andExpect(jsonPath("$.first_name").value("First"))
                .andExpect(jsonPath("$.last_name").value("Last"))
                .andExpect(jsonPath("$.birthday").value("07/08/1992"))
                .andExpect(jsonPath("$.imei").value("356938035643809"))
                .andExpect(jsonPath("$.geofences").value(null));
    }

}