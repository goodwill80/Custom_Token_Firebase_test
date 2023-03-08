package com.jonathan.firebase_custom_token.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfigurationFilterChain {

    @Autowired
    FirebaseFilter firebaseFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/signup/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(firebaseFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors();
        http.oauth2ResourceServer().jwt();
        return http.build();
    }

}
