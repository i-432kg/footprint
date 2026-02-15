package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT id, name, login_id, hashed_password, authority, birthdate, email, is_active, disabled, disabled_at, created_at FROM users WHERE login_id = #{login_id}")
    Optional<User> findByLoginId(@Param("login_id") final LoginId loginId);

    @Select("SELECT COUNT(*) FROM users WHERE login_id = #{login_id}")
    int countByLoginId(@Param("login_id") LoginId loginId);

    @Insert("INSERT INTO users(name, login_id, hashed_password, authority, birthdate) VALUES(#{name}, #{login_id}, #{hashed_password}, #{authority}, #{birthdate})")
    void insert(
            @Param("name") UserName userName,
            @Param("login_id") LoginId loginId,
            @Param("hashed_password") HashedPassword hashedPassword,
            @Param("authority") Authority authority,
            @Param("birthdate") BirthDate birthdate
    );

}
