package jp.i432kg.footprint.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * {@link org.springframework.web.multipart.MultipartFile} が
 * {@code null} ではなく、かつ空ファイルではないことを検証する Bean Validation アノテーションです。
 */
@Documented
@Constraint(validatedBy = NotEmptyFileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFile {

    /**
     * 検証失敗時のメッセージを返します。
     *
     * @return 検証エラーメッセージ
     */
    String message() default "ファイルを選択してください";

    /**
     * 検証グループを返します。
     *
     * @return 検証グループ
     */
    Class<?>[] groups() default {};

    /**
     * クライアントコード向けのメタデータ payload を返します。
     *
     * @return payload 定義
     */
    Class<? extends Payload>[] payload() default {};

}
