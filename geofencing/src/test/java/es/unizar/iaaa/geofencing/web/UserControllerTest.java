package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.unizar.iaaa.geofencing.domain.security.LoginUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import es.unizar.iaaa.geofencing.domain.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final User USER1 = new User(null, "example.gmail.com", "password", "First",
            "Last", "07/08/1992", "356938035643809", new HashSet<>());


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        userRepository.deleteAll();
    }

    @Test
    public void createUser() throws Exception {
        this.mockMvc.perform(post("/api/users")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(USER1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(USER1.getEmail()))
                .andExpect(jsonPath("$.pass").value(USER1.getPass()))
                .andExpect(jsonPath("$.first_name").value(USER1.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(USER1.getLast_name()))
                .andExpect(jsonPath("$.birthday").value(USER1.getBirthday()))
                .andExpect(jsonPath("$.imei").value(USER1.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty());
        assertEquals(1, userRepository.count());
    }

    // TODO Autenticación
    @Test
    public void modifyUser() throws Exception {
        User usuario = userRepository.save(USER1);
        usuario.setBirthday("07/08/1994");
        this.mockMvc.perform(put("/api/users/"+usuario.getId())
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(usuario)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.pass").value(usuario.getPass()))
                .andExpect(jsonPath("$.first_name").value(usuario.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(usuario.getLast_name()))
                .andExpect(jsonPath("$.birthday").value(usuario.getBirthday()))
                .andExpect(jsonPath("$.imei").value(usuario.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty());
        User usuarioNew = userRepository.findOne(usuario.getId());
        assertEquals(usuario.getBirthday(), usuarioNew.getBirthday());
    }

    // TODO Autenticación
    @Test
    public void deleteUser() throws Exception {
        User usuario = userRepository.save(USER1);
        this.mockMvc.perform(delete("/api/users/"+usuario.getId()))
                .andExpect(status().isOk());
        assertNull(userRepository.findOne(usuario.getId()));
    }

    // TODO Dos vistas: con y sin Autenticación
    @Test
    public void getUser() throws Exception {
        User usuario = userRepository.save(USER1);
        this.mockMvc.perform(get("/api/users/"+usuario.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.pass").value(usuario.getPass()))
                .andExpect(jsonPath("$.first_name").value(usuario.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(usuario.getLast_name()))
                .andExpect(jsonPath("$.birthday").value(usuario.getBirthday()))
                .andExpect(jsonPath("$.imei").value(usuario.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty());
    }

    @Test
    public void authenticateUser() throws Exception {
        LoginUser usuario = new LoginUser("user@gmail.com", "password");
        this.mockMvc.perform(post("/api/users/authenticate")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(usuario)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.username").value(usuario.getEmail()));
    }
}