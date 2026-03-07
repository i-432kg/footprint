package jp.i432kg.footprint.application.command;

import jakarta.transaction.Transactional;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ユーザーに関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 新しいユーザーを作成し、システムに登録します。
     * 登録前にメールアドレスの重複チェックを行い、パスワードをセキュアにハッシュ化した上で保存します。
     *
     * @param command ユーザー作成に必要な情報を含むコマンドオブジェクト
     * @throws Exception 指定されたメールアドレスが既に登録されている場合
     */
    @Transactional
    public void createUser(final CreateUserCommand command) throws Exception {

        // メールアドレスの重複チェック
        if (userDomainService.isEmailAlreadyUsed(command.getEmail())) {
            throw new Exception();
        }

        final User user = User.of(
                command.getUserName(),
                command.getEmail(),
                toHashedPassword(command.getRawPassword()),
                command.getBirthDate()
        );

        userRepository.saveUser(user);
    }

    /**
     * 平文のパスワードを暗号化する
     *
     * @param rawPassword ハッシュ化前の生パスワード
     * @return ハッシュ化されたパスワード
     */
    private HashedPassword toHashedPassword(final RawPassword rawPassword) {
        return HashedPassword.of(passwordEncoder.encode(rawPassword.value()));
    }
}