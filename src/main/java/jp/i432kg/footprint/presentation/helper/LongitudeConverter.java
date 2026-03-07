package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.Longitude;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class LongitudeConverter implements Converter<String, Longitude> {

    @Override
    public Longitude convert(final String source) {
        return Objects.isNull(source) || source.isBlank()
                ? null
                : Longitude.of(new BigDecimal(source));
    }
}
