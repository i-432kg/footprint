package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * ユーザー情報の参照専用クエリを行う Mapper インターフェース
 */
@Mapper
public interface UserQueryMapper {

    /**
     * ユーザーIDに基づいてユーザープロフィールを検索します。
     *
     * @param userId ユーザー ID
     * @return ユーザープロフィールの参照専用モデル
     */
    Optional<UserProfileSummary> findProfileByUserId(@Param("userId") UserId userId);
}
