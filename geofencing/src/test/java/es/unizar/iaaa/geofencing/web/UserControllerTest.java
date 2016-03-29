package es.unizar.iaaa.geofencing.web;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;

import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void test1CreateUser() throws Exception {
        User usuario = new User(1, "example.gmail.com", "password", "First", "Last", "07/08/1992", "356938035643809", new HashSet<Geofence>());
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
                .andExpect(jsonPath("$.geofences").isEmpty());
    }

    @Test
    public void test2ModifyUser() throws Exception {
        User usuario = new User(1, "example.gmail.com", "pass", "Second", "Last", "07/08/1994", "356938035643809", new HashSet<Geofence>());
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        this.mockMvc.perform(put("/api/users/1").contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("example.gmail.com"))
                .andExpect(jsonPath("$.pass").value("pass"))
                .andExpect(jsonPath("$.first_name").value("Second"))
                .andExpect(jsonPath("$.last_name").value("Last"))
                .andExpect(jsonPath("$.birthday").value("07/08/1994"))
                .andExpect(jsonPath("$.imei").value("356938035643809"))
                .andExpect(jsonPath("$.geofences").isEmpty());
    }

    @Test
    public void test3DeleteUser() throws Exception {
        this.mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void test4GetUser() throws Exception {
        this.mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("example.gmail.com"))
                .andExpect(jsonPath("$.pass").value("password"))
                .andExpect(jsonPath("$.first_name").value("First"))
                .andExpect(jsonPath("$.last_name").value("Last"))
                .andExpect(jsonPath("$.birthday").value("07/08/1992"))
                .andExpect(jsonPath("$.imei").value("356938035643809"))
                .andExpect(jsonPath("$.geofences").isEmpty());
    }
}