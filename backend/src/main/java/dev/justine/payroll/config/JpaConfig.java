package dev.justine.payroll.config;

import dev.justine.payroll.common.CurrentUser;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditor")
public class JpaConfig {

    /** Who is changing data: the Keycloak username from the JWT, or "system" for background jobs. */
    @Bean
    AuditorAware<String> auditor() {
        return () -> Optional.of(CurrentUser.username().orElse("system"));
    }
}
