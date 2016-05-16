package es.unizar.iaaa.geofencing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.HashSet;

import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;

@Component
public class DatabaseFillerOnStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String PASSWORD = "admin";

    private User ADMIN = new User(null, "admin", null, "First", "Last", Date.valueOf("1992-08-07"),
            "", new HashSet<>(), true, "admin", new HashSet<>(), new HashSet<>());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findByUsername("admin") == null) {
            ADMIN.setPassword(passwordEncoder.encode(PASSWORD));
            userRepository.save(ADMIN);
        }
    }
}