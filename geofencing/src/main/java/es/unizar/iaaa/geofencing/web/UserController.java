package es.unizar.iaaa.geofencing.web;

import es.unizar.iaaa.geofencing.model.User;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UserController {

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
    public ResponseEntity<User> createUser(@RequestBody User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(5).toUri());
        return new ResponseEntity<>(new User(user.getId(), user.getEmail(), user.getPass(), user.getFirst_name(),
                user.getLast_name(), user.getBirthday(), user.getImei()), httpHeaders, HttpStatus.CREATED);
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
    public User modifyUser(@PathVariable("id") int id, @RequestBody User user) {
        if (id > 4) {
            return new User(id, user.getEmail(), user.getPass(), user.getFirst_name(), user.getLast_name(),
                    user.getBirthday(), user.getImei());
        } else if (id == 4) {
            throw new UserNotModifiedException();
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
    public User deleteUser(@PathVariable("id") int id) {
        if (id == 4) {
            return new User(id, "", "", "", "", "", "");
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
    public User getUser(@PathVariable("id") int id) {
        if (id == 4) {
            return new User(id, "", "", "", "", "", "");
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method logs user into the system.
     *
     * @param email email of the user
     * @param pass  password of the user
     * @return the user logged in
     */
    @RequestMapping(path = "/api/users/login", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged in", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User loginUser(@RequestParam("email") String email, @RequestParam("pass") String pass) {
        if (email.equals("email") && pass.equals("pass")) {
            return new User(1, email, pass, "", "", "", "");
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * This method logs user out of the system.
     *
     * @param email email of the user
     * @param pass  password of the user
     * @return the user logged out
     */
    @RequestMapping(path = "/api/users/logout", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged out", response = User.class),
            @ApiResponse(code = 404, message = "User not found", response = UserNotFoundException.class)})
    public User logoutUser(@RequestParam("email") String email, @RequestParam("pass") String pass) {
        if (email.equals("email") && pass.equals("pass")) {
            return new User(1, email, pass, "", "", "", "");
        } else {
            throw new UserNotFoundException();
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_MODIFIED, reason = "Not modified")
    public class UserNotModifiedException extends RuntimeException { }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
    public class UserNotFoundException extends RuntimeException { }

}