package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    private String parentReplyId;

    /**
     * 返信本文
     */
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^(?!.*[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]).*$")
    private String message;
}
