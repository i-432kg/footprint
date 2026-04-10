package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    void of_shouldCreateInstanceWithGivenValues() {
        Location actual = Location.of(
                DomainTestFixtures.latitude(),
                DomainTestFixtures.longitude()
        );

        assertThat(actual.getLatitude()).isEqualTo(DomainTestFixtures.latitude());
        assertThat(actual.getLongitude()).isEqualTo(DomainTestFixtures.longitude());
    }

    @Test
    void unknown_shouldCreateLocationWithNullCoordinates() {
        final Location actual = Location.unknown();

        assertThat(actual.getLatitude()).isNull();
        assertThat(actual.getLongitude()).isNull();
        assertThat(actual.isUnknown()).isTrue();
    }

}
