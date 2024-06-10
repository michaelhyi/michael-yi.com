package com.michaelyi.auth;

import com.michaelyi.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.michaelyi.util.Constants.ADMIN_EMAIL;

@Configuration
public class AuthConfig {
    private final String adminPassword;

    public AuthConfig(@Value("${auth.admin-pw}") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Bean
    public User adminUser() {
        return new User(adminPassword);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            boolean authorized = username.equals(ADMIN_EMAIL);

            if (!authorized) {
                throw new UsernameNotFoundException("User not found.");
            }

            return adminUser();
        };
    }
}