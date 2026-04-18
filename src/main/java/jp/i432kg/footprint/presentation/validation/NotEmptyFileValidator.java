package jp.i432kg.footprint.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * {@link NotEmptyFile} の検証ロジックを提供する validator です。
 */
public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFile, MultipartFile> {

    /**
     * 対象ファイルが {@code null} ではなく、かつ空でない場合にのみ妥当と判定します。
     *
     * @param value 検証対象のアップロードファイル
     * @param context Bean Validation の検証コンテキスト
     * @return ファイルが選択されている場合は {@code true}
     */
    @Override
    public boolean isValid(final MultipartFile value, final ConstraintValidatorContext context) {
        return Objects.nonNull(value) && !value.isEmpty();
    }

}
