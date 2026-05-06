package jp.i432kg.footprint.presentation.validation;

import jakarta.validation.Constraint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

class NotEmptyFileTest {

    @Test
    @DisplayName("NotEmptyFile は想定どおりのデフォルトメッセージを持つ")
    void should_haveDefaultMessage_when_annotationIsRead() throws NoSuchMethodException {
        final String actual = (String) NotEmptyFile.class.getMethod("message").getDefaultValue();

        assertThat(actual).isEqualTo("ファイルを選択してください");
    }

    @Test
    @DisplayName("NotEmptyFile は NotEmptyFileValidator を validator として参照する")
    void should_referenceNotEmptyFileValidator_when_annotationIsRead() {
        final Constraint constraint = NotEmptyFile.class.getAnnotation(Constraint.class);

        assertThat(constraint).isNotNull();
        assertThat(constraint.validatedBy()).containsExactly(NotEmptyFileValidator.class);
    }

    @Test
    @DisplayName("NotEmptyFile は FIELD 対象かつ RUNTIME 保持である")
    void should_targetFieldAndRuntime_when_annotationMetadataIsRead() {
        final Target target = NotEmptyFile.class.getAnnotation(Target.class);
        final Retention retention = NotEmptyFile.class.getAnnotation(Retention.class);

        assertThat(target).isNotNull();
        assertThat(target.value()).containsExactly(ElementType.FIELD);
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }

}
