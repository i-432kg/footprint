package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.infrastructure.datasource.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public int countUser(UserName userName) {
        return userMapper.countByName(userName);
    }

    @Override
    public void saveUser(User user) {
        userMapper.insert(user.getName(), user.getHashedPassword(), user.getAuthority(), user.getBirthDate());
    }
}
