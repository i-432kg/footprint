package jp.i432kg.footprint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * 現在時刻の取得に利用する {@link Clock} を提供する設定です。
 * <p>
 * application / infrastructure で直接 `now()` を呼ばず、
 * 注入可能な時刻境界として扱うための共通 Bean を定義します。
 */
@Configuration
public class TimeConfig {

    /**
     * システム標準の現在時刻取得に利用する {@link Clock} を返します。
     *
     * @return `Asia/Tokyo` タイムゾーンの {@link Clock}
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Tokyo"));
    }
}
