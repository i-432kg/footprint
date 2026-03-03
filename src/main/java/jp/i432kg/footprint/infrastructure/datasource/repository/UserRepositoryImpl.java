package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * ユーザーに関するリポジトリの実装クラス
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public int countUser(final EmailAddress email) {
        return userMapper.countByEmail(email);
    }

    @Override
    public void saveUser(final User user) {

        // ユーザーレコードを保存する
        final UserMapper.UserInsertEntity entity = UserMapper.UserInsertEntity.from(user);
        userMapper.insert(entity);
    }
}
