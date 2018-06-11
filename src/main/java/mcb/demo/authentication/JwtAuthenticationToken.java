package mcb.demo.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationToken implements Authentication {

    private boolean authenticated;
    private DecodedJWT jwt;

    public JwtAuthenticationToken(DecodedJWT jwt) {
        this.jwt = jwt;
        authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return jwt.getClaims().get("roles").asList(String.class).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public DecodedJWT getDetails() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return jwt.getSubject();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated && jwt.getExpiresAt().after(new Date());
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        if (!authenticated) {
            this.authenticated = false;
        }
    }

    @Override
    public String getName() {
        return null;
    }
}
