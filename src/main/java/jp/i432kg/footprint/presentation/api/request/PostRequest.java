package jp.i432kg.footprint.presentation.api.request;

import jp.i432kg.footprint.presentation.validation.NotEmptyFile;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * 投稿作成リクエスト
 */
@Getter
@Setter
public class PostRequest {

    /**
     * 投稿画像
     */
    @NotEmptyFile
    private MultipartFile imageFile;

    /**
     * 投稿本文
     */
    @NonNull
    private String comment;
}
