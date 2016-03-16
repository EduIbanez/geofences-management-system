package es.unizar.iaaa.geofencing.web;

import es.unizar.iaaa.geofencing.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    /**
     * This method creates a new user.
     *
     * @param user data of the user
     * @return the user created
     */
    @RequestMapping(path = "/api/users", method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        boolean created = true;
        if (created) {
            return new ResponseEntity<>(new User(user.getId(), user.getEmail(), user.getPass(), user.getFirst_name(),
                    user.getLast_name(), user.getBirthday(), user.getImei()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method modifies the data of a previously created user.
     *
     * @param id   unique identifier representing a specific user
     * @param user data of the user
     * @return the user modified
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<User> modifyUser(@PathVariable("id") int id, @RequestBody User user) {
        boolean found = true;
        boolean modified = true;
        if (found) {
            if (modified) {
                return new ResponseEntity<>(new User(user.getId(), user.getEmail(), user.getPass(), user.getFirst_name(),
                        user.getLast_name(), user.getBirthday(), user.getImei()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This method deletes the data of a previously created user.
     *
     * @param id unique identifier representing a specific user
     * @return the user deleted
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable("id") int id) {
        boolean found = true;
        boolean deleted = true;
        if (found) {
            if (deleted) {
                return new ResponseEntity<>(new User(id, "", "", "", "", "", ""), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This method returns a user by id.
     *
     * @param id unique identifier representing a specific user
     * @return the user requested
     */
    @RequestMapping(path = "/api/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        boolean found = true;
        if (found) {
            return new ResponseEntity<>(new User(id, "", "", "", "", "", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<User> loginUser(@RequestParam("email") String email, @RequestParam("pass") String pass) {
        boolean checked = true;
        if (checked) {
            return new ResponseEntity<>(new User(1, email, pass, "", "", "", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<User> logoutUser(@RequestParam("email") String email, @RequestParam("pass") String pass) {
        boolean checked = true;
        if (checked) {
            return new ResponseEntity<>(new User(1, email, pass, "", "", "", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}