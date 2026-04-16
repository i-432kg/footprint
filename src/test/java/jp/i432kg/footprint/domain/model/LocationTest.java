package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    @DisplayName("Location.of は既知の位置情報を生成する")
    void should_createKnownLocation_when_latitudeAndLongitudeAreGiven() {
        final Location actual = Location.of(
                DomainTestFixtures.latitude(),
                DomainTestFixtures.longitude()
        );

        assertThat(actual.getLatitude()).isEqualTo(DomainTestFixtures.latitude());
        assertThat(actual.getLongitude()).isEqualTo(DomainTestFixtures.longitude());
        assertThat(actual.isUnknown()).isFalse();
    }

    @Test
    @DisplayName("Location.unknown は不明な位置情報を生成する")
    void should_createUnknownLocation_when_unknownIsCalled() {
        final Location actual = Location.unknown();

        assertThat(actual.getLatitude()).isNull();
        assertThat(actual.getLongitude()).isNull();
        assertThat(actual.isUnknown()).isTrue();
    }

    @Test
    @DisplayName("Location.unknown は同一の不明位置インスタンスを返す")
    void should_returnSameInstance_when_unknownIsCalledMultipleTimes() {
        final Location first = Location.unknown();
        final Location second = Location.unknown();

        assertThat(first).isSameAs(second);
    }
}
