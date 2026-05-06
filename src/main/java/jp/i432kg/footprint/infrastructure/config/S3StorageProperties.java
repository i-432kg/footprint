package jp.i432kg.footprint.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage.s3")
public class S3StorageProperties {

    /**
     * 例: https://storage.railway.app
     */
    private String endpoint;

    /**
     * 例: ap-northeast-1
     * Railway の値をそのまま受ける想定
     */
    private String region;

    /**
     * バケット名
     */
    private String bucketName;

    /**
     * access key
     */
    private String accessKey;

    /**
     * secret key
     */
    private String secretKey;

    /**
     * path-style を強制するか
     * Railway は通常 false 想定
     */
    private boolean pathStyle = false;

    /**
     * 署名付きURLの有効期限（分）
     */
    private long presignedGetExpireMinutes = 30;
}