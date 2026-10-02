package dev.justine.payroll.payroll.storage;

import java.time.Instant;

/** Port for file storage, so the payroll logic doesn't know it's S3 (and tests can swap it). */
public interface PayslipStorage {

    record DownloadLink(String url, Instant expiresAt) {}

    void put(String key, byte[] pdf);

    boolean exists(String key);

    DownloadLink presignedDownload(String key, String downloadFileName);
}
