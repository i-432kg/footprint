package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.presentation.validation.NotEmptyFile;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 投稿作成 API へ渡す multipart リクエスト DTO です。
 * <p>
 * 画像ファイルは必須で、コメントは 100 文字以内かつ制御文字を含まない前提で受け付けます。
 */
@Getter
@Setter
public class PostRequest {

    /**
     * 投稿画像ファイルです。
     * <p>
     * `null` および空ファイルは許可しません。
     */
    @NotNull
    @NotEmptyFile
    private MultipartFile imageFile;

    /**
     * 投稿コメントです。
     * <p>
     * 未指定は許容しますが、指定する場合は 100 文字以内かつ制御文字を含まない必要があります。
     */
    @Size(max = 100)
    @Pattern(regexp = PresentationValidationPatterns.NO_CONTROL_CHARS)
    private String comment;
}
