package dev.justine.payroll.payroll.storage;

import dev.justine.payroll.config.AppProperties;
import java.time.Clock;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/**
 * Payslips are private objects. The browser never gets AWS credentials; it gets a pre-signed URL that
 * is valid for a few minutes and only for that one object.
 */
@Component
public class S3PayslipStorage implements PayslipStorage {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final AppProperties props;
    private final Clock clock;

    public S3PayslipStorage(S3Client s3, S3Presigner presigner, AppProperties props, Clock clock) {
        this.s3 = s3;
        this.presigner = presigner;
        this.props = props;
        this.clock = clock;
    }

    @Override
    public void put(String key, byte[] pdf) {
        // Same key for the same employee+period: a retried run overwrites instead of creating duplicates.
        s3.putObject(PutObjectRequest.builder()
                .bucket(props.storage().bucket())
                .key(key)
                .contentType("application/pdf")
                .build(),
            RequestBody.fromBytes(pdf));
    }

    @Override
    public boolean exists(String key) {
        try {
            s3.headObject(HeadObjectRequest.builder().bucket(props.storage().bucket()).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public DownloadLink presignedDownload(String key, String downloadFileName) {
        var request = GetObjectPresignRequest.builder()
            .signatureDuration(props.storage().presignTtl())
            .getObjectRequest(GetObjectRequest.builder()
                .bucket(props.storage().bucket())
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + downloadFileName + "\"")
                .build())
            .build();
        var signed = presigner.presignGetObject(request);
        return new DownloadLink(signed.url().toString(), clock.instant().plus(props.storage().presignTtl()));
    }
}
