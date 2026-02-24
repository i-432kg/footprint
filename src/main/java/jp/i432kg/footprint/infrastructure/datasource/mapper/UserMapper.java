package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("""
        SELECT
            id,
            username AS name,
            email AS loginId,
            password_hash AS hashedPassword,
            birthdate AS birthDate,
            is_active AS isActive,
            disabled AS isDisabled,
            disabled_at AS disabledAt,
            created_at AS createdAt
        FROM users
        WHERE email = #{email}
        """)
    Optional<User> findByLoginId(@Param("email") final LoginId email);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByLoginId(@Param("email") LoginId email);

    @Insert("""
        INSERT INTO users (username, email, password_hash, birthdate, is_active, created_at, updated_at)
        VALUES (#{username}, #{email}, #{passwordHash}, #{birthdate}, #{isActive}, #{createdAt}, #{updatedAt})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserInsertEntity params);

    /**
     * User 用の Insert パラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class UserInsertEntity {
        private Long id;
        private final UserName username;
        private final LoginId email;
        private final HashedPassword passwordHash;
        private final BirthDate birthdate;
        private final boolean isActive;
        private final java.time.LocalDateTime createdAt;
        private final java.time.LocalDateTime updatedAt;

        public static UserInsertEntity from(final User user) {
            return new UserInsertEntity(
                    null,
                    user.getName(),
                    user.getLoginId(),
                    user.getHashedPassword(),
                    user.getBirthDate(),
                    true, // デフォルトで有効
                    java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now()
            );
        }
    }

}
