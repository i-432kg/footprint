package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImageLoader;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.Objects;

/**
 * STG seed 用の元画像や manifest を S3 から取得するコンポーネントです。
 */
@Component
@Profile("stg")
@RequiredArgsConstructor
public class S3SeedSourceImageProvider implements SeedSourceImageLoader {

    private final S3Client s3Client;
    private final StgSeedProperties properties;

    /**
     * 指定したオブジェクトキーのファイルを S3 から取得します。
     *
     * @param objectKey S3 オブジェクトキー
     * @return seed 用入力ストリームと元ファイル名
     */
    public SeedSourceImage load(final String objectKey) {
        final String bucketName = properties.getSourceBucketName();
        if (Objects.isNull(bucketName) || bucketName.isBlank()) {
            throw new IllegalStateException("app.stg-seed.source-bucket-name must be configured.");
        }

        final ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build()
        );

        return new SeedSourceImage(responseInputStream, extractFilename(objectKey));
    }

    /**
     * オブジェクトキーからファイル名部分を抽出する。
     *
     * @param objectKey S3 オブジェクトキー
     * @return ファイル名
     */
    private String extractFilename(final String objectKey) {
        final int lastSlashIndex = objectKey.lastIndexOf('/');
        return (lastSlashIndex >= 0) ? objectKey.substring(lastSlashIndex + 1) : objectKey;
    }
}
