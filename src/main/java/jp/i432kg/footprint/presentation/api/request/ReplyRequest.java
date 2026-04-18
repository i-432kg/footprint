package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * 返信作成 API へ渡す JSON リクエスト DTO です。
 * <p>
 * ルート返信とネスト返信の両方を表現し、`parentReplyId` がない場合はルート返信として扱います。
 */
@Getter
@Setter
public class ReplyRequest {

    /**
     * 親返信 ID です。
     * <p>
     * 未指定の場合はルート返信として扱い、指定する場合は ULID 形式である必要があります。
     */
    @Nullable
    @Pattern(regexp = PresentationValidationPatterns.ULID)
    private String parentReplyId;

    /**
     * 返信本文です。
     * <p>
     * 必須項目であり、100 文字以内かつ制御文字を含まない必要があります。
     */
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = PresentationValidationPatterns.NO_CONTROL_CHARS)
    private String message;
}
