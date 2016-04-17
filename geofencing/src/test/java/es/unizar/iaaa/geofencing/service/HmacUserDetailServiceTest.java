package es.unizar.iaaa.geofencing.service;

import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.SecurityUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class HmacUserDetailServiceTest {

    @InjectMocks
    private HmacUserDetailsService hmacUserDetailsService;

    @Before
    public void init(){
        MockUsers.mock();
    }

    @Test
    public void loadByUserName(){
        String username = "admin@gmail.com";
        UserDetails userDetails = hmacUserDetailsService.loadUserByUsername(username);
        Assert.assertNotNull(userDetails);
        Assert.assertTrue(userDetails.getClass().isAssignableFrom(SecurityUser.class));

        SecurityUser securityUser = (SecurityUser) userDetails;
        AuthenticatedUser authenticatedUser = MockUsers.findByUsername(username);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertEquals(authenticatedUser.getId(),securityUser.getId());
        Assert.assertEquals(authenticatedUser.getEmail(),securityUser.getUsername());
        Assert.assertEquals(authenticatedUser.getProfile(),securityUser.getProfile());
        Assert.assertNotNull(authenticatedUser.getPassword());
        Assert.assertNotNull(securityUser.getAuthorities());
        Assert.assertTrue(!securityUser.getAuthorities().isEmpty());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadByWrongUserName(){
        hmacUserDetailsService.loadUserByUsername("unknown");
    }
}
