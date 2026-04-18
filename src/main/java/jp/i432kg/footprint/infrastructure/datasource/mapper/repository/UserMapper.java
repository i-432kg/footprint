package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * ユーザーに関する Mybatis マッパーインターフェース
 */
@Mapper
public interface UserMapper {

    /**
     * 指定されたユーザーIDを基に有効なユーザーの登録数を取得します。
     *
     * @param userId ユーザー ID
     * @return 検索にヒットした件数
     */
    int countByUserId(@Param("userId") UserId userId);

    /**
     * 指定されたメールアドレスの登録数を取得します。
     *
     * @param email 検索対象のメールアドレス
     * @return 検索にヒットした件数
     */
    int countByEmail(@Param("email") EmailAddress email);

    /**
     * ユーザーレコードを挿入します。
     */
    void insert(UserInsertEntity params);

    /**
     * User 用の Insert パラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class UserInsertEntity {
        private Long id;
        private UserId userId;
        private final UserName username;
        private final EmailAddress email;
        private final HashedPassword passwordHash;
        private final BirthDate birthdate;
        private final boolean isActive;
        private final boolean isDisabled;
        private final LocalDateTime disabledAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static UserInsertEntity from(final User user, final Clock clock) {
            return new UserInsertEntity(
                    null,
                    user.getUserId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getHashedPassword(),
                    user.getBirthDate(),
                    false,  // デフォルトで無効
                    false,          // デフォルトで無効
                    null,
                    LocalDateTime.now(clock),
                    LocalDateTime.now(clock)
            );
        }
    }

}
