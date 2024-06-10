package com.michaelyi.auth;

import com.michaelyi.security.JwtService;
import com.michaelyi.user.User;
import com.michaelyi.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    @Value("${auth.admin-pw}")
    private final String adminPassword;
    private final JwtService jwtService;
    private final User adminUser;

    public String login(LoginRequest req) {
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean authorized = encoder.matches(req.password(), adminPassword);

        if (!authorized) {
            throw new UnauthorizedException();
        }

        return jwtService.generateToken(adminUser);
    }

    public void validateToken(String bearerToken) {
        String token = bearerToken.substring(Constants.BEARER_PREFIX_LENGTH);

        if (!jwtService.isTokenExpired(token)) {
            throw new UnauthorizedException();
        }
    }
}
