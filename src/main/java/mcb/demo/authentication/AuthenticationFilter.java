package mcb.demo.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    static final String SECRET_KEY = "some-secret";
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(header) && header.startsWith("Bearer ")) {
            String token = header.split("Bearer ")[1];

            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            try {
                JWTVerifier jwtVerifier = JWT.require(algorithm)
                        .withIssuer("auth0")
                        .build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                Authentication auth = authenticationManager.authenticate(new JwtAuthenticationToken(decodedJWT));
                if (auth.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JWTVerificationException ignored) {

            }
        }
        filterChain.doFilter(request, response);
    }
}
