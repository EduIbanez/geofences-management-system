package es.unizar.iaaa.geofencing.service;

import es.unizar.iaaa.geofencing.domain.hmac.HmacException;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.LoginUser;
import es.unizar.iaaa.geofencing.domain.security.Profile;
import es.unizar.iaaa.geofencing.domain.security.SecurityUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.crypto.*")
@PrepareForTest({SecurityContextHolder.class})
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletResponse httpResponse;

    private SecurityUser getSecurityUser(String email, String password){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new SecurityUser(Long.valueOf(1), email, password, Profile.ADMIN, authorities);
    }

    @Before
    public void init(){
        MockUsers.mock();
    }

    @Test
    public void authenticate() throws HmacException {

        LoginUser loginUser = new LoginUser("admin@gmail.com", "password");

        SecurityUser securityUser = getSecurityUser(loginUser.getEmail(), loginUser.getPassword());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword());

        PowerMockito.when(userDetailsService.loadUserByUsername(loginUser.getEmail())).thenReturn(securityUser);
        PowerMockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(token);

        AuthenticatedUser authenticatedUser = authenticationService.authenticate(loginUser, httpResponse);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertEquals(authenticatedUser.getEmail(), loginUser.getEmail());
        Assert.assertNotNull(authenticatedUser.getAuthorities());
        Assert.assertTrue(!authenticatedUser.getAuthorities().isEmpty());

        Mockito.verify(authenticationManager,Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(userDetailsService,Mockito.times(1)).loadUserByUsername(loginUser.getEmail());
        Mockito.verify(httpResponse,Mockito.times(3)).setHeader(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void logout(){

        Long userId = Long.valueOf(1);

        AuthenticatedUser authenticatedUser = MockUsers.findById(userId);
        Assert.assertNotNull(authenticatedUser);
        authenticatedUser.setSecretKey("secretKey");
        Assert.assertNotNull(authenticatedUser.getSecretKey());

        PowerMockito.mockStatic(SecurityContextHolder.class);
        UsernamePasswordAuthenticationToken passwordAuthenticationToken = PowerMockito.mock(UsernamePasswordAuthenticationToken.class);
        SecurityContextImpl securityContext = PowerMockito.mock(SecurityContextImpl.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(passwordAuthenticationToken);
        Mockito.when(passwordAuthenticationToken.isAuthenticated()).thenReturn(true);
        Mockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Mockito.when(passwordAuthenticationToken.getPrincipal()).thenReturn(new SecurityUser(Long.valueOf(1), "login@gmail.com", "password", Profile.ADMIN, Arrays.asList(new SimpleGrantedAuthority("ADMIN"))));

        authenticationService.logout();

        authenticatedUser = MockUsers.findById(userId);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertNull(authenticatedUser.getSecretKey());

    }

    @Test
    public void authenticateByToken(){

        LoginUser loginUser = new LoginUser("admin@gmail.com", "password");

        SecurityUser securityUser = getSecurityUser(loginUser.getEmail(), loginUser.getPassword());
        PowerMockito.when(userDetailsService.loadUserByUsername(loginUser.getEmail())).thenReturn(securityUser);

        authenticationService.tokenAuthentication(loginUser.getEmail());
        Assert.assertNotNull(SecurityContextHolder.getContext());
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().isAssignableFrom(SecurityUser.class));
        Assert.assertEquals(SecurityContextHolder.getContext().getAuthentication().getPrincipal(),securityUser);
    }
}
