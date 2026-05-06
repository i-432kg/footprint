package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * ユーザー永続化用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface UserMapper {

    /**
     * 指定したユーザー ID の登録件数を取得する。
     *
     * @param userId ユーザー ID
     * @return 検索にヒットした件数
     */
    int countByUserId(@Param("userId") UserId userId);

    /**
     * 指定したメールアドレスの登録件数を取得する。
     *
     * @param email メールアドレス
     * @return 検索にヒットした件数
     */
    int countByEmail(@Param("email") EmailAddress email);

    /**
     * ユーザーレコードを登録する。
     *
     * @param params 登録するユーザーパラメータ
     */
    void insert(UserInsertEntity params);

    /** ユーザー insert 用のパラメータ。 */
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
