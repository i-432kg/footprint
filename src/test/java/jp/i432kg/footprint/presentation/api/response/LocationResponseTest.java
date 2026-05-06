package jp.i432kg.footprint.presentation.api.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationResponseTest {

    @Test
    @DisplayName("LocationResponse は設定された緯度経度を保持する")
    void should_holdValues_when_latAndLngArePresent() {
        final LocationResponse response = LocationResponse.of(35.681236, 139.767125);

        assertThat(response.getLat()).isEqualTo(35.681236);
        assertThat(response.getLng()).isEqualTo(139.767125);
    }

    @Test
    @DisplayName("LocationResponse は null の緯度経度を保持できる")
    void should_allowNullCoordinates_when_latAndLngAreNull() {
        final LocationResponse response = LocationResponse.of(null, null);

        assertThat(response.getLat()).isNull();
        assertThat(response.getLng()).isNull();
    }

}
