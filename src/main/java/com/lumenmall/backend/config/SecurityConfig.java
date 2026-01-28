package com.lumenmall.backend.config;

import com.lumenmall.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.lumenmall.backend.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Product Management (Admin Only)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // User Profile
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile/update").authenticated()

                        // Public Auth Routes
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        // Public Product Reading
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // --- REVIEWS & COMMENTS ---
                        .requestMatchers(HttpMethod.GET, "/api/reviews/all").hasRole("ADMIN") // Add this
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasRole("ADMIN") // Add this
                        
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()
                        // Allow everyone to SEE reviews (Fixes the 403 error)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()
                        // Require login to POST a review or comment
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()

                        // Payments & Orders
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/orders/**").permitAll()

                        // Order Management (Admin Only)
                        .requestMatchers("/api/orders/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")


                        // Fallback
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(java.util.List.of(
                "http://localhost:5173",
                "https://lumen-mall-client.onrender.com" // Existing
        ));

        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}