package jp.i432kg.footprint.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEmptyFileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFile {

    String message() default "ファイルを選択してください";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
