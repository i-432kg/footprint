package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * ユーザー参照用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface UserQueryMapper {

    /**
     * ユーザー ID に基づいてユーザープロフィールを検索する。
     *
     * @param userId ユーザー ID
     * @return ユーザープロフィールの参照専用モデル
     */
    Optional<UserProfileSummary> findProfileByUserId(@Param("userId") UserId userId);
}
