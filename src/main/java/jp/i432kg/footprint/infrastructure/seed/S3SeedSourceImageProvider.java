package jp.i432kg.footprint.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.Objects;

/**
 * S3 上に事前配置した seed 用元画像を取得するコンポーネント。
 */
@Component
@Profile("stg")
@RequiredArgsConstructor
public class S3SeedSourceImageProvider {

    private final S3Client s3Client;
    private final StgSeedProperties properties;

    public SeedSourceImage load(final String objectKey) {
        final String bucketName = properties.getSourceBucketName();
        if (Objects.isNull(bucketName) || bucketName.isBlank()) {
            throw new IllegalStateException("app.seed.source-bucket-name must be configured.");
        }

        final ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build()
        );

        return new SeedSourceImage(responseInputStream, extractFilename(objectKey));
    }

    private String extractFilename(final String objectKey) {
        final int lastSlashIndex = objectKey.lastIndexOf('/');
        return (lastSlashIndex >= 0) ? objectKey.substring(lastSlashIndex + 1) : objectKey;
    }
}
