package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ユーザープロフィールのクエリモデルをレスポンス形式に変換するマッパー
 */
@Component
public class UserProfileResponseMapper {

    /**
     * {@link UserProfileSummary} を {@link UserProfileItemResponse} に変換します。
     *
     * @param summary ユーザープロフィールの参照専用モデル
     * @return ユーザープロフィールのレスポンス。引数が null の場合は null を返します。
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