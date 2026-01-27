package jp.i432kg.footprint.presentation.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class ReplyRequest {

    @Nullable
    private Integer parentReplyId;

    @NonNull
    private String content;
}
