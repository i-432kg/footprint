package jp.i432kg.footprint.application.command.service;

import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.application.exception.usecase.UserCommandFailedException;
import jp.i432kg.footprint.application.port.id.UserIdGenerator;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.logging.LoggingCategories;
import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザーに関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private static final Logger APP_LOGGER = LoggerFactory.getLogger(LoggingCategories.APP);

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdGenerator userIdGenerator;

    /**
     * 新しいユーザーを作成し、システムに登録します。
     * 登録前にメールアドレスの重複チェックを行い、パスワードをセキュアにハッシュ化した上で保存します。
     *
     * @param command ユーザー作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createUser(final CreateUserCommand command) {

        // メールアドレスの重複チェック
        userDomainService.ensureEmailNotAlreadyUsed(command.getEmail());

        // UserId を生成 (ULID)
        final UserId userId = userIdGenerator.generate();

        // User ドメインモデルを構築し、DBに永続化する
        final User user = User.of(
                userId,
                command.getUserName(),
                command.getEmail(),
                toHashedPassword(command.getRawPassword()),
                command.getBirthDate()
        );

        try {
            userRepository.saveUser(user);
        } catch (DataAccessException e) {
            throw UserCommandFailedException.saveFailed(e);
        }

        APP_LOGGER.atInfo()
                .addKeyValue("event", LoggingEvents.USER_CREATE_SUCCESS)
                .addKeyValue("userId", user.getUserId().getValue())
                .addKeyValue("username", user.getUserName().getValue())
                .log("User created");
    }

    /**
     * 平文のパスワードを暗号化する
     *
     * @param rawPassword ハッシュ化前の生パスワード
     * @return ハッシュ化されたパスワード
     */
    private HashedPassword toHashedPassword(final RawPassword rawPassword) {
        return HashedPassword.of(passwordEncoder.encode(rawPassword.getValue()));
    }
}
