package dev.justine.payroll.config;

import static dev.justine.payroll.config.Roles.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Stateless OAuth2 resource server: every request must carry a Keycloak-issued JWT.
 * Coarse URL rules live here; ownership rules ("only my payslip") live in the services.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http, AppProperties props) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsSource(props)))
            .csrf(csrf -> csrf.disable())     // no cookies/sessions: bearer tokens are not sent automatically, so CSRF doesn't apply
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/departments/**", "/api/employees/**").hasAnyRole(HR, PAYROLL_ADMIN)
                .requestMatchers("/api/employees/**").hasRole(HR)
                .requestMatchers("/api/payroll-runs/**").hasRole(PAYROLL_ADMIN)
                .requestMatchers("/api/me/**", "/api/payslips/**").authenticated()
                .anyRequest().denyAll())      // secure by default: anything not listed is rejected
            .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }

    private CorsConfigurationSource corsSource(AppProperties props) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(props.corsAllowedOrigins());
        cors.addAllowedMethod("*");
        cors.addAllowedHeader("*");
        cors.addExposedHeader("Location");
        cors.addExposedHeader("X-Correlation-Id");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }
}
