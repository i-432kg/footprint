package jp.i432kg.footprint.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3Config {

    @Bean
    public S3Client s3Client(final S3StorageProperties properties) {
        validate(properties);

        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                )
                .forcePathStyle(properties.isPathStyle())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(final S3StorageProperties properties) {
        validate(properties);

        return S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                )
                .build();
    }

    private void validate(final S3StorageProperties properties) {
        require(properties.getEndpoint(), "APP_STORAGE_S3_ENDPOINT");
        require(properties.getBucketName(), "APP_STORAGE_S3_BUCKET_NAME");
        require(properties.getRegion(), "APP_STORAGE_S3_REGION");
        require(properties.getAccessKey(), "APP_STORAGE_S3_ACCESS_KEY");
        require(properties.getSecretKey(), "APP_STORAGE_S3_SECRET_KEY");
    }

    private void require(final String value, final String envName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(
                    "S3 storage is enabled, but required environment variable is missing: " + envName
            );
        }
    }
}