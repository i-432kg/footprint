package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.Coordinate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CoordinateConverter implements Converter<String, Coordinate> {

    @Override
    public Coordinate convert(final String source) {
        return Objects.isNull(source) || source.isBlank() ? null : Coordinate.of(Double.parseDouble(source));
    }
}