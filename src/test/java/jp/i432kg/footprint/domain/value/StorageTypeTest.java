package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class StorageTypeTest {

    @Test
    @DisplayName("StorageType.of は LOCAL を大文字小文字を区別せず解釈する")
    void should_returnLocal_when_valueIsLocalIgnoringCase() {
        assertThat(StorageType.of("LOCAL")).isEqualTo(StorageType.LOCAL);
        assertThat(StorageType.of("local")).isEqualTo(StorageType.LOCAL);
    }

    @Test
    @DisplayName("StorageType.of は S3 を大文字小文字を区別せず解釈する")
    void should_returnS3_when_valueIsS3IgnoringCase() {
        assertThat(StorageType.of("S3")).isEqualTo(StorageType.S3);
        assertThat(StorageType.of("s3")).isEqualTo(StorageType.S3);
    }

    @Test
    @DisplayName("StorageType.of は null を拒否する")
    void should_throwException_when_storageTypeIsNull() {
        assertInvalidValue(() -> StorageType.of(null), "storage_type", "required");
    }

    @Test
    @DisplayName("StorageType.of は未知の値を拒否する")
    void should_throwException_when_storageTypeIsUnknown() {
        assertInvalidValue(() -> StorageType.of("gcs"), "storage_type", "unknown storage type");
    }
}
