package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import org.springframework.stereotype.Component;

/**
 * ユーザープロフィール query model を API レスポンス DTO へ変換する mapper です。
 */
@Component
public class UserProfileResponseMapper {

    /**
     * ユーザープロフィール query model をレスポンスへ変換します。
     *
     * @param summary ユーザープロフィール query model
     * @return ユーザープロフィールレスポンス
     */
    public UserProfileItemResponse from(final UserProfileSummary summary) {
        return UserProfileItemResponse.of(
                summary.getId(),
                summary.getUsername(),
                summary.getEmail(),
                summary.getPostCount(),
                summary.getReplyCount()
        );
    }
}
