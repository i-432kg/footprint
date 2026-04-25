package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidModelException;
import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoundingBoxTest {

    @Test
    @DisplayName("BoundingBox.of は妥当な境界ボックスを生成する")
    void should_createBoundingBox_when_valuesAreValid() {
        final BoundingBox actual = BoundingBox.of(
                DomainTestFixtures.latitude(),
                Latitude.of(new BigDecimal("35.700000")),
                DomainTestFixtures.longitude(),
                Longitude.of(new BigDecimal("139.800000"))
        );

        assertThat(actual.getMinLat()).isEqualTo(DomainTestFixtures.latitude());
        assertThat(actual.getMaxLat()).isEqualTo(Latitude.of(new BigDecimal("35.700000")));
        assertThat(actual.getMinLng()).isEqualTo(DomainTestFixtures.longitude());
        assertThat(actual.getMaxLng()).isEqualTo(Longitude.of(new BigDecimal("139.800000")));
    }

    @Test
    @DisplayName("BoundingBox.of は minLat が maxLat を超える場合に例外を送出する")
    void should_throwException_when_minLatIsGreaterThanMaxLat() {
        assertThatThrownBy(() -> BoundingBox.of(
                Latitude.of(new BigDecimal("35.800000")),
                Latitude.of(new BigDecimal("35.700000")),
                DomainTestFixtures.longitude(),
                Longitude.of(new BigDecimal("139.800000"))
        ))
                .isInstanceOf(InvalidModelException.class)
                .hasMessage("boundingBox is invalid. reason=min_lat_gt_max_lat");
    }

    @Test
    @DisplayName("BoundingBox.of は minLng が maxLng を超える場合に例外を送出する")
    void should_throwException_when_minLngIsGreaterThanMaxLng() {
        assertThatThrownBy(() -> BoundingBox.of(
                DomainTestFixtures.latitude(),
                Latitude.of(new BigDecimal("35.700000")),
                Longitude.of(new BigDecimal("139.900000")),
                Longitude.of(new BigDecimal("139.800000"))
        ))
                .isInstanceOf(InvalidModelException.class)
                .hasMessage("boundingBox is invalid. reason=min_lng_gt_max_lng");
    }
}
