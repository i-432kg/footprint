package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.Latitude;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class LatitudeConverter implements Converter<String, Latitude> {

    @Override
    public Latitude convert(final String source) {
        return Objects.isNull(source) || source.isBlank()
                ? null
                : Latitude.of(new BigDecimal(source));
    }
}
