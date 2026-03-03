package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.ReplyId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReplyIdConverter implements Converter<String, ReplyId> {

    @Override
    public ReplyId convert(final String source) {
        return Objects.isNull(source) || source.isBlank() ? null : ReplyId.of(Integer.parseInt(source));
    }
}
