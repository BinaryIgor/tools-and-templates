package io.codyn.app.template.user.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
public class AppSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(r -> r.requestMatchers("/users").permitAll()
                        .anyRequest()
                        .authenticated())
//                .addFilter(new AuthenticationFilter())
//                .ad
                .addFilterAfter(new AuthenticationFilter(), BasicAuthenticationFilter.class)
                .build();
    }
}
