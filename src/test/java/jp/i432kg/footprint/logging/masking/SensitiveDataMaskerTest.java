package jp.i432kg.footprint.logging.masking;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.HashedPassword;
import jp.i432kg.footprint.domain.value.RawPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SensitiveDataMaskerTest {

    private final SensitiveDataMasker masker = new SensitiveDataMasker();

    @Test
    @DisplayName("SensitiveDataMasker は objectKey 系の値をマスクする")
    void should_maskObjectKeyLikeValue_when_targetIndicatesObjectKey() {
        final Object actual = masker.maskRejectedValue(
                "objectKey",
                "users/u/posts/p/images/i.jpg"
        );

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("SensitiveDataMasker は fileName 系の値をマスクする")
    void should_maskFileNameLikeValue_when_targetIndicatesFileName() {
        final Object actual = masker.maskRejectedValue(
                "fileName",
                "my-private-photo.jpg"
        );

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("SensitiveDataMasker は details 内の storageObjectKey を再帰的にマスクする")
    void should_maskNestedStorageObjectKey_when_maskingMap() {
        final Map<String, Object> actual = masker.maskMap(Map.of(
                "target", "image.objectKey",
                "reason", "image_metadata_extract_failed",
                "rejectedValue", "users/u/posts/p/images/i.jpg",
                "storageObjectKey", "users/u/posts/p/images/i.jpg"
        ));

        assertThat(actual)
                .containsEntry("rejectedValue", "********")
                .containsEntry("storageObjectKey", "********");
    }

    @Test
    @DisplayName("SensitiveDataMasker は rejectedValue が null のとき null を返す")
    void should_returnNull_when_rejectedValueIsNull() {
        final Object actual = masker.maskRejectedValue("password", null);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("SensitiveDataMasker は EmailAddress を型ベースでマスクする")
    void should_maskEmailAddressByType_when_valueIsEmailAddress() {
        final Object actual = masker.maskRejectedValue(
                "userName",
                EmailAddress.of("alice@example.com")
        );

        assertThat(actual).isEqualTo("a********@example.com");
    }

    @Test
    @DisplayName("SensitiveDataMasker は RawPassword を型ベースでマスクする")
    void should_maskRawPasswordByType_when_valueIsRawPassword() {
        final Object actual = masker.maskRejectedValue(
                "userName",
                RawPassword.of("Password1!")
        );

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("SensitiveDataMasker は HashedPassword を型ベースでマスクする")
    void should_maskHashedPasswordByType_when_valueIsHashedPassword() {
        final Object actual = masker.maskRejectedValue(
                "userName",
                HashedPassword.of("$2a$10$abcdefghijklmnopqrstuvABCDEFGHIJKLMN0123456789abcd")
        );

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("SensitiveDataMasker はネストした Map 内の rejectedValue を再帰的にマスクする")
    void should_maskNestedRejectedValue_when_nestedMapContainsTargetAndRejectedValue() {
        final Map<String, Object> actual = masker.maskMap(Map.of(
                "errors", List.of(Map.of(
                        "target", "email",
                        "reason", "already_used",
                        "rejectedValue", "alice@example.com"
                ))
        ));

        assertThat(actual)
                .extractingByKey("errors")
                .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(Map.class))
                .first()
                .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.map(String.class, Object.class))
                .containsEntry("rejectedValue", "a********@example.com");
    }

    @Test
    @DisplayName("SensitiveDataMasker は List 内の非機微単純値を変更しない")
    void should_keepPlainListItem_when_listContainsNonSensitiveScalar() {
        final Map<String, Object> actual = masker.maskMap(Map.of(
                "errors", List.of("plain-text")
        ));

        assertThat(actual)
                .containsEntry("errors", List.of("plain-text"));
    }

    @Test
    @DisplayName("SensitiveDataMasker は機微情報でない値を変更しない")
    void should_keepOriginalValue_when_targetIsNotSensitive() {
        final Object actual = masker.maskRejectedValue("caption", "hello");

        assertThat(actual).isEqualTo("hello");
    }

    @Test
    @DisplayName("SensitiveDataMasker は不変 Map を返す")
    void should_returnUnmodifiableMap_when_maskMapIsCalled() {
        final Map<String, Object> actual = masker.maskMap(new LinkedHashMap<>(Map.of(
                "target", "caption",
                "reason", "plain_text",
                "rejectedValue", "hello"
        )));

        assertThatThrownBy(() -> actual.put("another", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
