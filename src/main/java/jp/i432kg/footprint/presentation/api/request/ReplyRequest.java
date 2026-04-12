package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * 返信作成リクエスト
 */
@Getter
@Setter
public class ReplyRequest {

    /**
     * 返信元の返信 ID
     */
    @Nullable
    @Pattern(regexp = PresentationValidationPatterns.ULID)
    private String parentReplyId;

    /**
     * 返信本文
     */
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = PresentationValidationPatterns.NO_CONTROL_CHARS)
    private String message;
}
