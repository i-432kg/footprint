package jp.i432kg.footprint.logging.masking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingTargetTest {

    @Test
    @DisplayName("MaskingTarget.PASSWORD は password 系キーに一致する")
    void should_matchPasswordLikeKey_when_passwordTargetReceivesPasswordKeyword() {
        assertThat(MaskingTarget.PASSWORD.matches("rawPassword")).isTrue();
        assertThat(MaskingTarget.PASSWORD.matches("client_secret")).isTrue();
    }

    @Test
    @DisplayName("MaskingTarget.TOKEN は token 系キーに一致する")
    void should_matchTokenLikeKey_when_tokenTargetReceivesTokenKeyword() {
        assertThat(MaskingTarget.TOKEN.matches("refreshToken")).isTrue();
        assertThat(MaskingTarget.TOKEN.matches("apiCredential")).isTrue();
    }

    @Test
    @DisplayName("MaskingTarget.EMAIL は email 系キーに一致する")
    void should_matchEmailLikeKey_when_emailTargetReceivesEmailKeyword() {
        assertThat(MaskingTarget.EMAIL.matches("email")).isTrue();
        assertThat(MaskingTarget.EMAIL.matches("contactEmailAddress")).isTrue();
    }

    @Test
    @DisplayName("MaskingTarget.FILE は objectKey と fileName 系キーに一致する")
    void should_matchFileLikeKey_when_fileTargetReceivesObjectKeyOrFileNameKeyword() {
        assertThat(MaskingTarget.FILE.matches("objectKey")).isTrue();
        assertThat(MaskingTarget.FILE.matches("image.fileName")).isTrue();
        assertThat(MaskingTarget.FILE.matches("storageObjectKey")).isTrue();
    }

    @Test
    @DisplayName("MaskingTarget.matches はキーの大小文字差を吸収する")
    void should_matchIgnoringCase_when_targetReceivesUpperCaseKey() {
        assertThat(MaskingTarget.FILE.matches("FileName")).isTrue();
        assertThat(MaskingTarget.FILE.matches("ObjectKey")).isTrue();
    }

    @Test
    @DisplayName("MaskingTarget は対象外キーに一致しない")
    void should_notMatch_when_keyDoesNotContainAnyKeyword() {
        assertThat(MaskingTarget.PASSWORD.matches("userName")).isFalse();
        assertThat(MaskingTarget.TOKEN.matches("userName")).isFalse();
        assertThat(MaskingTarget.EMAIL.matches("userName")).isFalse();
        assertThat(MaskingTarget.FILE.matches("userName")).isFalse();
    }

    @Test
    @DisplayName("MaskingTarget.mask は対応する MaskingStrategy へ委譲する")
    void should_delegateMaskingToStrategy_when_maskIsInvoked() {
        final Object actual = MaskingTarget.EMAIL.mask("alice@example.com");

        assertThat(actual).isEqualTo("a********@example.com");
    }
}
