package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.PostId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PostIdConverter implements Converter<String, PostId> {

    @Override
    public PostId convert(final String source) {
        return Objects.isNull(source) || source.isBlank() ? null : PostId.of(Integer.parseInt(source));
    }
}
