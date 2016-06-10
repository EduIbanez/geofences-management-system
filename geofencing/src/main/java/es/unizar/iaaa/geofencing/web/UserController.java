package es.unizar.iaaa.geofencing.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import es.unizar.iaaa.geofencing.view.View;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
                .fromCurrentRequest().path("/{id_user}")
                .buildAndExpand(user.getNick()).toUri());
        return new ResponseEntity<>(userCreated, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * This method modifies the data of a previously created user.
     *
     * @param id_user unique identifier name representing a specific user
     * @param user    data of the user
     * @return the user modified
     */
    @RequestMapping(path = "/api/users/{id_user:.+}", method = RequestMethod.PUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User modified", response = User.class),
            @ApiResponse(code = 304, message = "User not modified", response = UserNotModifiedException.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User modifyUser(@PathVariable("id_user") String id_user, @RequestBody User user) {
        LOGGER.info("Requested /api/users/{id_user} PUT method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String nick = customUser.getUsername();
        if (nick.equals(id_user) && userRepository.existsByUsername(id_user)) {
            User userRequested = userRepository.findByUsername(id_user);
            if (!user.getPassword().equals("")) {
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                userRequested.setPassword(hashedPassword);
            }
            userRequested.setFirstName(user.getFirstName());
            userRequested.setLastName(user.getLastName());
            userRequested.setBirthday(user.getBirthday());
            User userModified = userRepository.save(userRequested);
            if (userRequested.equals(userModified)) {
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
     * @param id_user unique identifier name representing a specific user
     * @return the user deleted
     */
    @RequestMapping(path = "/api/users/{id_user:.+}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User deleteUser(@PathVariable("id_user") String id_user) {
        LOGGER.info("Requested /api/users/{id_user} DELETE method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String nick = customUser.getUsername();
        if (nick.equals(id_user) && userRepository.existsByUsername(id_user)) {
            userRepository.delete(id_user);
            return null;
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method returns a user by id.
     *
     * @param id_user unique identifier name representing a specific user
     * @return the user requested
     */
    @RequestMapping(path = "/api/users/{id_user:.+}", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User requested", response = MappingJacksonValue.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public MappingJacksonValue getUser(@PathVariable("id_user") String id_user) {
        LOGGER.info("Requested /api/users/{id_user} GET method");
        if (userRepository.existsByUsername(id_user)) {
            final MappingJacksonValue result = new MappingJacksonValue(userRepository.findByUsername(id_user));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if ((auth instanceof AnonymousAuthenticationToken)) {
                result.setSerializationView(View.UserBaseView.class);
            } else {
                UserDetails customUser = (UserDetails) auth.getPrincipal();
                String nick = customUser.getUsername();
                if (nick.equals(id_user)) {
                    result.setSerializationView(View.UserCompleteView.class);
                } else {
                    result.setSerializationView(View.UserBaseView.class);
                }
            }
            return result;
        } else {
            throw new UserNotFoundException();
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_MODIFIED, reason = "Not modified")
    public class UserNotModifiedException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
    public class UserNotFoundException extends RuntimeException {
    }
}