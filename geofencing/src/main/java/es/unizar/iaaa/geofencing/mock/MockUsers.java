package es.unizar.iaaa.geofencing.mock;

import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.Profile;
import org.springframework.beans.BeanUtils;

import java.util.*;

@SuppressWarnings("unchecked")
public class MockUsers {

    private static List<AuthenticatedUser> users = new ArrayList<>();

    private static Map<Profile,List<String>> authorities =  new HashMap(){{
        put(Profile.ADMIN,Arrays.asList("ROLE_ADMIN","ROLE_MANAGER","ROLE_USER"));
        put(Profile.USER,Arrays.asList("ROLE_USER"));
    }};

    public static void mock(){
        AuthenticatedUser admin = new AuthenticatedUser(Long.valueOf(1), "admin@gmail.com", "password", null, Profile.ADMIN);
        admin.setAuthorities(authorities.get(admin.getProfile()));
        users.add(admin);

        AuthenticatedUser user = new AuthenticatedUser(Long.valueOf(2), "user@gmail.com", "password", null, Profile.USER);
        users.add(user);
    }

    public static List<AuthenticatedUser> getUsers(){
        if(users.isEmpty()){
            mock();
        }
        return users;
    }

    /**
     * Find a user by username
     * @param username username
     * @return a AuthenticatedUser if found, null otherwise
     */
    public static AuthenticatedUser findByUsername(String username){
        for(AuthenticatedUser authenticatedUser : users){
            if(authenticatedUser.getEmail().equals(username)){
                return authenticatedUser;
            }
        }
        return null;
    }

    /**
     * Find a user by id
     * @param id user id
     * @return a AuthenticatedUser if found, null otherwise
     */
    public static AuthenticatedUser findById(Long id){
        for(AuthenticatedUser authenticatedUser : users){
            if(authenticatedUser.getId().equals(id)){
                return authenticatedUser;
            }
        }
        return null;
    }

    /**
     * Update a given user
     * @param newAuthenticatedUser new user
     * @return updated user
     */
    public static AuthenticatedUser update(AuthenticatedUser newAuthenticatedUser){
        AuthenticatedUser existingUser = findById(newAuthenticatedUser.getId());
        if(existingUser != null){
            BeanUtils.copyProperties(newAuthenticatedUser, existingUser, "password");
            existingUser.setAuthorities(authorities.get(existingUser.getProfile()));
        }
        return null;
    }
}
