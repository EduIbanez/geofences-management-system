package es.unizar.iaaa.geofencing.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import es.unizar.iaaa.geofencing.view.View;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * This method creates a new user.
     *
     * @param user data of the user
     * @return the user created
     */
    @RequestMapping(path = "/api/users", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created",
                    responseHeaders = @ResponseHeader(name = "Location", description = "Location",
                            response = URI.class), response = User.class)})
    public ResponseEntity<User> createUser(@RequestBody final User user) {
        LOGGER.info("Requested /api/users POST method");
        user.setId(null);
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User userCreated = userRepository.save(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(userCreated, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * This method modifies the data of a previously created user.
     *
     * @param id   unique identifier representing a specific user
     * @param user data of the user
     * @return the user modified
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.PUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User modified", response = User.class),
            @ApiResponse(code = 304, message = "User not modified", response = UserNotModifiedException.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User modifyUser(@PathVariable("id") Long id, @RequestBody User user) {
        LOGGER.info("Requested /api/users/{id} PUT method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String email = customUser.getUsername();
        if (userRepository.existsByUsername(id, email)) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            User userModified = userRepository.save(user);
            if (user.equals(userModified)) {
                return userModified;
            } else {
                throw new UserNotModifiedException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method deletes the data of a previously created user.
     *
     * @param id unique identifier representing a specific user
     * @return the user deleted
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User deleteUser(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/users/{id} DELETE method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String email = customUser.getUsername();
        if (userRepository.existsByUsername(id, email)) {
            userRepository.delete(id);
            return null;
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method returns a user by id.
     *
     * @param id unique identifier representing a specific user
     * @return the user requested
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User requested", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public MappingJacksonValue getUser(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/users/{id} GET method");
        if (userRepository.exists(id)) {
            final MappingJacksonValue result = new MappingJacksonValue(userRepository.findOne(id));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if ((auth instanceof AnonymousAuthenticationToken)) {
                result.setSerializationView(View.UserBaseView.class);
            } else {
                result.setSerializationView(View.UserCompleteView.class);
            }
            return result;
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method authenticate user into the system.
     *
     * @param email email of the user
     * @param password password of the user
     * @return the user logged in
     */
    @RequestMapping(path = "/api/users/authenticate", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User authenticated", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User authenticateUser(@RequestParam(value = "email") String email,
                                              @RequestParam(value = "password") String password) {
        User user = userRepository.findByUsernameAndPassword(email, password);
        if (user != null) {
            ArrayList<GrantedAuthority> authorities = new ArrayList();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(),
                    user.getPassword(), authorities));
            return user;
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method logs user out of the system.
     */
    /*@RequestMapping(path = "/api/users/logout", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged out", response = User.class)})
    public void logoutUser() {
        authenticationService.logout();
    }*/

    @ResponseStatus(value = HttpStatus.NOT_MODIFIED, reason = "Not modified")
    public class UserNotModifiedException extends RuntimeException { }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
    public class UserNotFoundException extends RuntimeException { }

}