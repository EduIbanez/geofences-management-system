package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private static final String PASSWORD = "password";

    private static final User USER1 = new User(null, "example.gmail.com", PASSWORD, "First",
            "Last", "07/08/1992", "356938035643809", new HashSet<>(), true, "user", new HashSet<>());


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userRepository.deleteAll();
    }

    @Test
    public void createUser() throws Exception {
        String hashedPassword = passwordEncoder.encode(USER1.getPassword());
        this.mockMvc.perform(post("/api/users")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(USER1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(USER1.getEmail()))
                .andExpect(jsonPath("$.password").value(hashedPassword))
                .andExpect(jsonPath("$.firstName").value(USER1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(USER1.getLastName()))
                .andExpect(jsonPath("$.birthday").value(USER1.getBirthday()))
                .andExpect(jsonPath("$.imei").value(USER1.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty())
                .andExpect(jsonPath("$.enabled").value(USER1.getEnabled()))
                .andExpect(jsonPath("$.role").value(USER1.getRole()))
                .andExpect(jsonPath("$.notifications").isEmpty());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void modifyUser() throws Exception {
        String hashedPassword = passwordEncoder.encode(USER1.getPassword());
        USER1.setPassword(hashedPassword);
        User usuario = userRepository.save(USER1);
        USER1.setPassword(PASSWORD);
        usuario.setPassword(PASSWORD);
        usuario.setBirthday("07/08/1994");
        this.mockMvc.perform(put("/api/users/"+usuario.getId())
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(usuario))
                .with(httpBasic(usuario.getEmail(), usuario.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.password").value(hashedPassword))
                .andExpect(jsonPath("$.firstName").value(usuario.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(usuario.getLastName()))
                .andExpect(jsonPath("$.birthday").value(usuario.getBirthday()))
                .andExpect(jsonPath("$.imei").value(usuario.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty())
                .andExpect(jsonPath("$.enabled").value(usuario.getEnabled()))
                .andExpect(jsonPath("$.role").value(usuario.getRole()))
                .andExpect(jsonPath("$.notifications").isEmpty());
        User usuarioNew = userRepository.findOne(usuario.getId());
        assertEquals(usuario.getBirthday(), usuarioNew.getBirthday());
    }

    @Test
    public void deleteUser() throws Exception {
        String hashedPassword = passwordEncoder.encode(PASSWORD);
        USER1.setPassword(hashedPassword);
        User usuario = userRepository.save(USER1);
        USER1.setPassword(PASSWORD);
        usuario.setPassword(PASSWORD);
        this.mockMvc.perform(delete("/api/users/"+usuario.getId())
                .with(httpBasic(usuario.getEmail(), usuario.getPassword())))
                .andExpect(status().isOk());
        assertNull(userRepository.findOne(usuario.getId()));
    }

    @Test
    public void getUserAuthenticated() throws Exception {
        String hashedPassword = passwordEncoder.encode(PASSWORD);
        USER1.setPassword(hashedPassword);
        User usuario = userRepository.save(USER1);
        USER1.setPassword(PASSWORD);
        usuario.setPassword(PASSWORD);
        this.mockMvc.perform(get("/api/users/"+usuario.getId())
                .with(httpBasic(usuario.getEmail(), usuario.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.password").value(hashedPassword))
                .andExpect(jsonPath("$.firstName").value(usuario.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(usuario.getLastName()))
                .andExpect(jsonPath("$.birthday").value(usuario.getBirthday()))
                .andExpect(jsonPath("$.imei").value(usuario.getImei()))
                .andExpect(jsonPath("$.geofences").isEmpty())
                .andExpect(jsonPath("$.enabled").value(usuario.getEnabled()))
                .andExpect(jsonPath("$.role").value(usuario.getRole()))
                .andExpect(jsonPath("$.notifications").isEmpty());
    }

    @Test
    public void getUserNotAuthenticated() throws Exception {
        String hashedPassword = passwordEncoder.encode(PASSWORD);
        USER1.setPassword(hashedPassword);
        User usuario = userRepository.save(USER1);
        USER1.setPassword(PASSWORD);
        usuario.setPassword(PASSWORD);
        this.mockMvc.perform(get("/api/users/"+usuario.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()))
                .andExpect(jsonPath("$.enabled").value(usuario.getEnabled()))
                .andExpect(jsonPath("$.role").value(usuario.getRole()));
    }
}