package es.unizar.iaaa.geofencing.service;

import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.SecurityUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmacUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AuthenticatedUser authenticatedUser = MockUsers.findByUsername(username);
        if (authenticatedUser == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if(!authenticatedUser.getAuthorities().isEmpty()){
            for(String authority : authenticatedUser.getAuthorities()){
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }

        return new SecurityUser(authenticatedUser.getId(), authenticatedUser.getEmail(),
                authenticatedUser.getPassword(), authenticatedUser.getProfile(), authorities);
    }
}
