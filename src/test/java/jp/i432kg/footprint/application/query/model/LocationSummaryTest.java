package jp.i432kg.footprint.application.query.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationSummaryTest {

    @Test
    @DisplayName("LocationSummary は設定された緯度経度を保持する")
    void should_holdValues_when_latAndLngAreSet() {
        final LocationSummary summary = new LocationSummary(35.681236, 139.767125);

        assertThat(summary.getLat()).isEqualTo(35.681236);
        assertThat(summary.getLng()).isEqualTo(139.767125);
    }

    @Test
    @DisplayName("LocationSummary は不明な位置情報として null を保持できる")
    void should_allowNullValues_when_locationIsUnknown() {
        final LocationSummary summary = new LocationSummary(null, null);

        assertThat(summary.getLat()).isNull();
        assertThat(summary.getLng()).isNull();
    }

    @Test
    @DisplayName("LocationSummary は no-args 生成時に null で初期化される")
    void should_initializeNullValues_when_createdWithNoArgsConstructor() {
        final LocationSummary summary = new LocationSummary();

        assertThat(summary.getLat()).isNull();
        assertThat(summary.getLng()).isNull();
    }
}
