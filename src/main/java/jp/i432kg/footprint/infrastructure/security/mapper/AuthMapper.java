package jp.i432kg.footprint.infrastructure.security.mapper;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ログイン認証（Spring Security）専用のデータベース操作を行う Mapper インターフェース
 */
@Mapper
public interface AuthMapper {

    /**
     * ログイン認証に必要な情報のみを取得します。
     */
    Optional<AuthUserEntity> findAuthUserByLoginId(@Param("email") final EmailAddress email);

    /**
     * 認証成功時に最終ログイン日時を更新します。
     */
    void updateLastLoginAt(@Param("userId") final UserId userId, @Param("lastLoginAt") final LocalDateTime lastLoginAt);

    /**
     * 認証用のパラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor
    class AuthUserEntity {
        private final UserId userId;
        private final String email;
        private final String displayUsername;
        private final String password;
    }
}
