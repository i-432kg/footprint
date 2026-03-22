package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @NotEmptyFile
    private MultipartFile imageFile;

    /**
     * 投稿本文
     */
    @Size(max = 100)
    private String comment;
}
