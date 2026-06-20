package sn.autocloser.infrastructure.adapter.in.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Pages statiques publiques
                .requestMatchers("/", "/index.html", "/dashboard.html", "/*.css", "/*.js").permitAll()
                // APIs publiques
                .requestMatchers("/api/v1/webhook/**").permitAll()
                .requestMatchers("/api/v1/onboarding/**").permitAll()
                .requestMatchers("/api/v1/ia/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll()  // Pour le MVP/dev - sécuriser avec JWT en prod
            );
        return http.build();
    }
}
