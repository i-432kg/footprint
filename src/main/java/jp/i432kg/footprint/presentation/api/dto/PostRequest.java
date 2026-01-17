package jp.i432kg.footprint.presentation.api.dto;

import jp.i432kg.footprint.presentation.validation.NotEmptyFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequest {

    @NotEmptyFile
    private MultipartFile imageFile;
    private String comment;
}
