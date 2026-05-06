package jp.i432kg.footprint.application.query.service;

import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.UserId;

import java.util.Optional;

/**
 * ユーザーの参照サービス
 */
public interface UserQueryService {

    /**
     * ユーザープロフィールを取得する。
     * ユーザーが存在しない場合は例外をスローします。
     *
     * @param userId 取得対象のユーザ ID
     * @return 指定したユーザのプロフィール情報
     * @throws UserNotFoundException ユーザーが存在しない場合
     */
    UserProfileSummary getUserProfile(UserId userId) throws UserNotFoundException;

    /**
     * ユーザープロフィールを検索する。
     *
     * @param userId 取得対象のユーザ ID
     * @return 指定したユーザのプロフィール情報（存在しない場合は empty）
     */
    Optional<UserProfileSummary> findUserProfile(UserId userId);

}
