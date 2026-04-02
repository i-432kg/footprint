package jp.i432kg.footprint.infrastructure.seed;

import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * STG 環境向けの seed データ投入サービス。
 * <p>
 * seed ユーザー、投稿、返信を作成し、既に存在する場合は重複作成しない。
 * 画像は S3 に配置済みの seed 元画像を利用する。
 * </p>
 */
@Slf4j
@Service
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedService {

    private static final String USER_EMAIL_DOMAIN = "example.com";

    private final StgSeedProperties properties;
    private final UserCommandService userCommandService;
    private final PostCommandService postCommandService;
    private final ReplyCommandService replyCommandService;
    private final SeedAdminMapper seedAdminMapper;
    private final S3SeedSourceImageProvider seedSourceImageProvider;

    /**
     * seed ユーザーと seed 投稿を作成する。
     */
    @Transactional
    public void seed() {
        final SeedUsers seedUsers = createSeedUsers();
        createPostsAndRepliesForActiveUsers(seedUsers.activeUsers());
    }

    /**
     * seed ユーザーを作成し、作成済みの場合は既存ユーザーを利用する。
     *
     * @return 作成した seed ユーザー群
     */
    private SeedUsers createSeedUsers() {
        final List<UserSeed> activeUsers = new ArrayList<>();
        final List<UserSeed> inactiveUsers = new ArrayList<>();

        int sequence = 1;

        for (int i = 1; i <= properties.getActiveUserCount(); i++) {
            activeUsers.add(createOrLoadUser(sequence, i, UserType.ACTIVE));
            sequence++;
        }

        for (int i = 1; i <= properties.getInactiveUserCount(); i++) {
            inactiveUsers.add(createOrLoadUser(sequence, i, UserType.INACTIVE));
            sequence++;
        }

        log.info("Seed users prepared. activeUserCount={}, inactiveUserCount={}", activeUsers.size(), inactiveUsers.size());

        return new SeedUsers(activeUsers, inactiveUsers);
    }

    /**
     * 投稿・返信ありユーザー向けの投稿と返信を作成する。
     * <p>
     * activeUserImageObjectKeys に定義された画像をすべて消費し、
     * 投稿先ユーザーへ順番に割り当てる。
     * </p>
     *
     * @param activeUsers 投稿・返信ありユーザー一覧
     */
    private void createPostsAndRepliesForActiveUsers(final List<UserSeed> activeUsers) {
        if (activeUsers.isEmpty()) {
            log.info("No active seed users configured. Skipping post/reply creation.");
            return;
        }

        final List<String> imageObjectKeys = properties.getActiveUserImageObjectKeys();
        if (imageObjectKeys == null || imageObjectKeys.isEmpty()) {
            throw new IllegalStateException("app.seed.active-user-image-object-keys must contain at least one object key when active users exist.");
        }

        final int[] postSequenceByUser = new int[activeUsers.size()];

        for (int imageIndex = 0; imageIndex < imageObjectKeys.size(); imageIndex++) {
            final int authorIndex = imageIndex % activeUsers.size();
            final UserSeed author = activeUsers.get(authorIndex);
            final int postSequence = ++postSequenceByUser[authorIndex];
            final String caption = seedCaption(author.username(), postSequence);
            final String imageObjectKey = imageObjectKeys.get(imageIndex);

            if (seedAdminMapper.findPostIdByCaption(caption).isEmpty()) {
                createPost(author.userId(), caption, imageObjectKey);
                log.info("Seed post created. email={}, caption={}, sourceObjectKey={}", author.email(), caption, imageObjectKey);
            }

            final String postId = seedAdminMapper.findPostIdByCaption(caption)
                    .orElseThrow(() -> new IllegalStateException("Seed post not found after create. caption=" + caption));

            final UserSeed replier = resolveReplier(activeUsers, authorIndex);
            final String replyMessage = seedReplyMessage(postSequence, replier.username());
            if (!seedAdminMapper.existsReplyByPostIdAndMessage(postId, replyMessage)) {
                replyCommandService.createReply(
                        CreateReplyCommand.of(
                                PostId.of(postId),
                                replier.userId(),
                                null,
                                Comment.of(replyMessage)
                        )
                );
                log.info("Seed reply created. postId={}, replier={}", postId, replier.email());
            }
        }
    }

    /**
     * 投稿への返信者を解決する。
     * <p>
     * active ユーザーが複数いる場合は次のユーザーを返信者にし、
     * 1人しかいない場合は同一ユーザーを返信者にする。
     * </p>
     *
     * @param activeUsers 投稿・返信ありユーザー一覧
     * @param authorIndex 投稿者の添字
     * @return 返信者
     */
    private UserSeed resolveReplier(final List<UserSeed> activeUsers, final int authorIndex) {
        if (activeUsers.size() == 1) {
            return activeUsers.get(0);
        }
        return activeUsers.get((authorIndex + 1) % activeUsers.size());
    }

    /**
     * seed ユーザーを作成し、作成済みの場合は既存ユーザーを返す。
     *
     * @param sequence 全体で一意な連番
     * @param typeSequence 種別ごとの連番
     * @param userType ユーザー種別
     * @return seed ユーザー
     */
    private UserSeed createOrLoadUser(final int sequence, final int typeSequence, final UserType userType) {
        final String email = seedEmail(sequence);
        final String username = seedUsername(userType, typeSequence);

        if (seedAdminMapper.findUserIdByEmail(email).isEmpty()) {
            userCommandService.createUser(
                    CreateUserCommand.of(
                            UserName.of(username),
                            EmailAddress.of(email),
                            RawPassword.of(properties.getTestPassword()),
                            BirthDate.of(LocalDate.of(1990, 1, Math.min(sequence, 28)))
                    )
            );
            log.info("Seed user created. email={}, username={}, type={}", email, username, userType);
        }

        final String userId = seedAdminMapper.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed user not found after create. email=" + email));

        return new UserSeed(UserId.of(userId), email, username, userType);
    }

    /**
     * seed 用画像を取得して投稿を作成する。
     *
     * @param userId 投稿者 ID
     * @param caption 投稿本文
     * @param sourceObjectKey 元画像のオブジェクトキー
     */
    private void createPost(final UserId userId, final String caption, final String sourceObjectKey) {
        try (SeedSourceImage seedSourceImage = seedSourceImageProvider.load(sourceObjectKey)) {
            postCommandService.createPost(
                    CreatePostCommand.of(
                            userId,
                            Comment.of(caption),
                            seedSourceImage.inputStream(),
                            FileName.of(seedSourceImage.originalFilename())
                    )
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close seed image stream. objectKey=" + sourceObjectKey, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create seed post. sourceObjectKey=" + sourceObjectKey, e);
        }
    }

    /**
     * seed 用メールアドレスを生成する。
     *
     * @param number 連番
     * @return メールアドレス
     */
    private String seedEmail(final int number) {
        return properties.getEmailPrefix() + String.format("%02d@%s", number, USER_EMAIL_DOMAIN);
    }

    /**
     * seed 用ユーザー名を生成する。
     *
     * @param userType ユーザー種別
     * @param number 種別内連番
     * @return ユーザー名
     */
    private String seedUsername(final UserType userType, final int number) {
        return String.format("stg%s%02d", userType.usernamePrefix(), number);
    }

    /**
     * seed 用投稿本文を生成する。
     *
     * @param username 投稿者名
     * @param postIndex 投稿連番
     * @return 投稿本文
     */
    private String seedCaption(final String username, final int postIndex) {
        return String.format("[STG] %s post%02d", username, postIndex);
    }

    /**
     * seed 用返信メッセージを生成する。
     *
     * @param postIndex 投稿連番
     * @param username 返信者名
     * @return 返信メッセージ
     */
    private String seedReplyMessage(final int postIndex, final String username) {
        return String.format("[STG] %s reply%02d", username, postIndex);
    }

    /**
     * seed 作成対象ユーザーの内部保持用レコード。
     *
     * @param userId ユーザー ID
     * @param email メールアドレス
     * @param username ユーザー名
     * @param userType ユーザー種別
     */
    private record UserSeed(UserId userId, String email, String username, UserType userType) {
    }

    /**
     * seed 作成対象ユーザー群の内部保持用レコード。
     *
     * @param activeUsers 投稿・返信ありユーザー
     * @param inactiveUsers 投稿・返信なしユーザー
     */
    private record SeedUsers(List<UserSeed> activeUsers, List<UserSeed> inactiveUsers) {
    }

    /**
     * seed ユーザー種別。
     */
    private enum UserType {
        ACTIVE("active"),
        INACTIVE("inactive");

        private final String usernamePrefix;

        UserType(final String usernamePrefix) {
            this.usernamePrefix = usernamePrefix;
        }

        private String usernamePrefix() {
            return usernamePrefix;
        }
    }
}
