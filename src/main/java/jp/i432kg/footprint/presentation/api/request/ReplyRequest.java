package jp.i432kg.footprint.presentation.api.request;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
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
    private Integer parentReplyId;

    /**
     * 返信本文
     */
    @NonNull
    private String message;
}
