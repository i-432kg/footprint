package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.LoginId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public int countUser(LoginId loginId) {
        return userMapper.countByLoginId(loginId);
    }

    @Override
    public void saveUser(User user) {
        // ユーザーレコードの保存
        final UserMapper.UserInsertEntity entity = UserMapper.UserInsertEntity.from(user);
        userMapper.insert(entity);
    }
}
