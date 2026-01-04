package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.user.User;
import jp.i432kg.footprint.domain.value.Authority;
import jp.i432kg.footprint.domain.value.HashedPassword;
import jp.i432kg.footprint.domain.value.UserName;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT id, name, hashed_password, authority, disabled, disabled_at, created_at FROM users WHERE name = #{name}")
    Optional<User> findByName(@Param("name") final UserName userName);

    @Insert("INSERT INTO users(name, hashed_password, authority) VALUES(#{name}, #{hashed_password}, #{authority})")
    void insert(@Param("name") UserName userName, @Param("hashed_password") HashedPassword hashedPassword, @Param("authority") Authority authority);

    @Select("SELECT COUNT(*) FROM users WHERE name = #{name}")
    int countByName(String name);

}
