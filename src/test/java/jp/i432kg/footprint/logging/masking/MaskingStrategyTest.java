package jp.i432kg.footprint.logging.masking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaskingStrategyTest {

    @Test
    @DisplayName("MaskingStrategy.FULL は任意の値を固定文字列でマスクする")
    void should_returnMaskedValue_when_fullStrategyMasksAnyValue() {
        final Object actual = MaskingStrategy.FULL.mask("secret-value");

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("MaskingStrategy.EMAIL は通常のメールアドレスのローカル部をマスクする")
    void should_maskLocalPart_when_emailStrategyReceivesNormalEmail() {
        final Object actual = MaskingStrategy.EMAIL.mask("alice@example.com");

        assertThat(actual).isEqualTo("a********@example.com");
    }

    @Test
    @DisplayName("MaskingStrategy.EMAIL はローカル部が 1 文字のメールアドレスを全文マスクする")
    void should_returnFullyMaskedValue_when_emailStrategyReceivesSingleCharLocalPart() {
        final Object actual = MaskingStrategy.EMAIL.mask("a@example.com");

        assertThat(actual).isEqualTo("********");
    }

    @Test
    @DisplayName("MaskingStrategy.EMAIL は @ を含まない値を全文マスクする")
    void should_returnFullyMaskedValue_when_emailStrategyReceivesValueWithoutAtMark() {
        final Object actual = MaskingStrategy.EMAIL.mask("invalid-mail");

        assertThat(actual).isEqualTo("********");
    }
}
