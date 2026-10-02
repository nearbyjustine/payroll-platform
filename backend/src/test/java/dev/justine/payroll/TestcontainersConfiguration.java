package dev.justine.payroll;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Real Postgres and real (emulated) S3 in throwaway containers. @ServiceConnection wires the DataSource
 * automatically; the S3 endpoint is pushed into app.storage.* with a DynamicPropertyRegistrar.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));
    }

    @Bean
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.9")).withServices("s3");
    }

    @Bean
    DynamicPropertyRegistrar s3Properties(LocalStackContainer localstack) {
        return registry -> {
            registry.add("app.storage.endpoint", () -> localstack.getEndpoint().toString());
            registry.add("app.storage.public-endpoint", () -> localstack.getEndpoint().toString());
            registry.add("app.storage.region", localstack::getRegion);
        };
    }
}
