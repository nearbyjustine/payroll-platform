package dev.justine.payroll.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Typed binding of the app.* block in application.yml. Fails fast at startup if a value can't be bound. */
@ConfigurationProperties(prefix = "app")
public record AppProperties(List<String> corsAllowedOrigins, Storage storage, Payroll payroll) {

    public record Storage(String bucket, String region, String endpoint, String publicEndpoint, Duration presignTtl) {}

    public record Payroll(int chunkSize) {}
}
