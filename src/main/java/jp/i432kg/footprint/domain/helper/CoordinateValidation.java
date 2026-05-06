package jp.i432kg.footprint.domain.helper;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 緯度・経度に共通する座標値の検証を提供するヘルパーです。
 */
public final class CoordinateValidation {

    private CoordinateValidation() {
    }

    /**
     * 座標値の null / 丸め / 範囲検証を行い、正規化済みの値を返します。
     *
     * @param fieldName フィールド名
     * @param value 検証対象の値
     * @param min 許容最小値
     * @param max 許容最大値
     * @param scale 小数点以下の桁数
     * @return 正規化済みの座標値
     */
    public static BigDecimal validate(
            final String fieldName,
            final @Nullable BigDecimal value,
            final BigDecimal min,
            final BigDecimal max,
            final int scale
    ) {
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(fieldName);
        }

        final BigDecimal roundedValue = value.setScale(scale, RoundingMode.HALF_UP);

        if (roundedValue.compareTo(min) < 0 || roundedValue.compareTo(max) > 0) {
            throw InvalidValueException.outOfRange(fieldName, roundedValue, min, max);
        }

        return roundedValue;
    }
}
