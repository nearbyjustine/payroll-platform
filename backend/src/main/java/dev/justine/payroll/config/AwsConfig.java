package dev.justine.payroll.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * S3 clients. Locally the endpoint points at LocalStack; in real AWS you leave it empty and the SDK
 * uses the regional endpoint plus the IAM role's credentials (never hard-coded keys).
 */
@Configuration
public class AwsConfig {

    @Bean
    S3Client s3Client(AppProperties props) {
        var builder = S3Client.builder()
            .region(Region.of(props.storage().region()))
            .credentialsProvider(credentials(props));
        if (StringUtils.hasText(props.storage().endpoint())) {
            builder.endpointOverride(URI.create(props.storage().endpoint()))
                   .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }
        return builder.build();
    }

    /** Signs URLs for the host the BROWSER can reach (inside Docker the API sees "localstack", the browser sees "localhost"). */
    @Bean
    S3Presigner s3Presigner(AppProperties props) {
        var builder = S3Presigner.builder()
            .region(Region.of(props.storage().region()))
            .credentialsProvider(credentials(props));
        String endpoint = StringUtils.hasText(props.storage().publicEndpoint())
            ? props.storage().publicEndpoint() : props.storage().endpoint();
        if (StringUtils.hasText(endpoint)) {
            builder.endpointOverride(URI.create(endpoint))
                   .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }
        return builder.build();
    }

    /**
     * LocalStack accepts any keys, so with an endpoint override we use dummy "test" credentials.
     * Against real AWS: the default chain (env vars, ~/.aws, or the IAM role of the EC2/ECS task).
     */
    private static AwsCredentialsProvider credentials(AppProperties props) {
        if (StringUtils.hasText(props.storage().endpoint())) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
        }
        return DefaultCredentialsProvider.builder().build();
    }
}
