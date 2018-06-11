package mcb.demo.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JwtAuthenticationManager implements AuthenticationManager, AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new AuthenticationServiceException("Cannot authenticate " + authentication.getClass());
        }
        JwtAuthenticationToken jwt = (JwtAuthenticationToken)authentication;
        if (LocalDateTime.ofEpochSecond(jwt.getDetails().getExpiresAt().getTime(), 0, ZoneOffset.UTC)
                .isBefore(LocalDateTime.now())) {
            throw new AuthenticationServiceException("Token has expired");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}
