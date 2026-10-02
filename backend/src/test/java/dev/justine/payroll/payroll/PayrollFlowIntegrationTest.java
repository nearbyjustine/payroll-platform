package dev.justine.payroll.payroll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import dev.justine.payroll.TestcontainersConfiguration;
import dev.justine.payroll.config.AppProperties;
import java.time.Duration;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

/**
 * The whole flow with real infrastructure: Postgres + S3 (LocalStack) containers, the async executor,
 * the after-commit event, chunked processing, PDF rendering and upload.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PayrollFlowIntegrationTest {

    @Autowired PayrollRunService runs;
    @Autowired PayslipRepository payslips;
    @Autowired S3Client s3;
    @Autowired AppProperties props;

    @Test
    void requestedRunIsProcessedAsynchronouslyAndPdfsLandInS3() {
        s3.createBucket(CreateBucketRequest.builder().bucket(props.storage().bucket()).build());
        var auth = new TestingAuthenticationToken("paolo", null, "ROLE_PAYROLL_ADMIN");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var accepted = runs.request(YearMonth.of(2026, 8));
        assertThat(accepted.status()).isEqualTo(RunStatus.PENDING);

        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofMillis(500))
            .until(() -> runs.get(accepted.id()).status() == RunStatus.COMPLETED);

        var done = runs.get(accepted.id());
        assertThat(done.processedCount()).isEqualTo(done.employeeCount()).isEqualTo(60);
        assertThat(done.totalGross().subtract(done.totalDeductions())).isEqualByComparingTo(done.totalNet());
        assertThat(done.requestedBy()).isEqualTo("paolo");

        var objects = s3.listObjectsV2(ListObjectsV2Request.builder()
            .bucket(props.storage().bucket()).prefix("payslips/2026-08/").build());
        assertThat(objects.keyCount()).isEqualTo(60);

        SecurityContextHolder.clearContext();
    }
}
