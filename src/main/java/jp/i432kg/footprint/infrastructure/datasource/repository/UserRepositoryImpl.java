package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.UserMapper;
import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Clock;

/**
 * {@link UserRepository} のデータソース永続化実装。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final Clock clock;

    @Override
    public boolean existsById(final UserId userId) {
        try {
            return userMapper.countByUserId(userId) > 0;
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.USER_EXISTS_CHECK_FAILED)
                    .addKeyValue("userId", userId.getValue())
                    .setCause(e)
                    .log("Failed to check user existence by id");
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(final EmailAddress email) {
        try {
            return userMapper.countByEmail(email) > 0;
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.USER_EMAIL_EXISTS_CHECK_FAILED)
                    .addKeyValue("email", email.getValue())
                    .setCause(e)
                    .log("Failed to check user existence by email");
            throw e;
        }
    }

    @Override
    public void saveUser(final User user) {
        try {
            // ユーザーレコードを保存する
            final UserMapper.UserInsertEntity entity = UserMapper.UserInsertEntity.from(user, clock);
            userMapper.insert(entity);
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.USER_SAVE_FAILED)
                    .addKeyValue("userId", user.getUserId().getValue())
                    .setCause(e)
                    .log("Failed to save user");
            throw e;
        }
    }
}
