package mcb.demo.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import mcb.demo.user.User;
import mcb.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/authenticate")
@Scope("request")
public class AuthenticationService {
    private UserRepository userRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        String username = userRepository.getByUsername(request.getUsername())
                .map(User::getUsername).orElseGet(() -> userRepository.create(request.getUsername()).getUsername());
        return ResponseEntity.ok(generateJWT(username));
    }

    private String generateJWT(String username) throws Exception {
            Algorithm algorithm = Algorithm.HMAC256(AuthenticationFilter.SECRET_KEY);
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withSubject(username)
                    .withExpiresAt(new Date(new Date().getTime() + 3600 * 1000))
                    .withClaim("username", username)
                    .sign(algorithm);
            return token;
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity validate(Authentication authentication) throws Exception {
        JwtAuthenticationToken token = (JwtAuthenticationToken)authentication;
        return ResponseEntity.ok(token.getDetails());

    }
}
