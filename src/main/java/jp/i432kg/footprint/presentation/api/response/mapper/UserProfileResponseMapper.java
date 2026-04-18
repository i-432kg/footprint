package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ユーザープロフィール query model を API レスポンス DTO へ変換する mapper です。
 */
@Component
public class UserProfileResponseMapper {

    /**
     * ユーザープロフィール query model をレスポンスへ変換します。
     *
     * @param summary ユーザープロフィール query model。{@code null} の場合は {@code null}
     * @return ユーザープロフィールレスポンス。引数が {@code null} の場合は {@code null}
     */
    public UserProfileItemResponse from(final UserProfileSummary summary) {
        return Optional.ofNullable(summary)
                .map(s -> UserProfileItemResponse.of(
                        s.getId(),
                        s.getUsername(),
                        s.getEmail(),
                        s.getPostCount(),
                        s.getReplyCount()
                ))
                .orElse(null);
    }
}
