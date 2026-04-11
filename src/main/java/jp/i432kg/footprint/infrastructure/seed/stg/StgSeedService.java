package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * STG 環境向けの固定シナリオ seed データを投入するサービスです。
 * <p>
 * 投稿あり/なし、返信あり/なしの状態を確認できる最小データセットを維持します。
 * </p>
 */
@Slf4j
@Service
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedService {

    private static final String USER_EMAIL_DOMAIN = "example.com";
    private static final String STG_LABEL = "[STG]";

    private final StgSeedProperties properties;
    private final UserCommandService userCommandService;
    private final PostCommandService postCommandService;
    private final ReplyCommandService replyCommandService;
    private final StgSeedAdminMapper seedAdminMapper;
    private final S3SeedSourceImageProvider seedSourceImageProvider;
    private final StgSeedImageManifestLoader seedImageManifestLoader;

    /**
     * STG 固定シナリオの seed ユーザー、投稿、返信を投入します。
     */
    @Transactional
    public void seed() {
        final List<String> imageObjectKeys = seedImageManifestLoader.loadObjectKeys();
        final UserSeed posterWithReplies = createOrLoadUser("poster_with_replies", "stg_post_reply", 1);
        final UserSeed posterWithoutReplies = createOrLoadUser("poster_without_replies", "stg_post_only", 2);
        final UserSeed nonPosterReplier = createOrLoadUser("non_poster_replier", "stg_reply_only", 3);

        final String postWithRepliesId = createOrLoadPost(
                posterWithReplies,
                seedCaption("post_with_replies"),
                selectObjectKey(imageObjectKeys, 0)
        );
        createOrLoadPost(
                posterWithoutReplies,
                seedCaption("post_without_replies"),
                selectObjectKey(imageObjectKeys, 1)
        );

        final String replyWithChildrenId = createOrLoadReply(
                postWithRepliesId,
                nonPosterReplier,
                ParentReply.root(),
                seedReplyMessage("reply_with_children")
        );
        createOrLoadReply(
                postWithRepliesId,
                posterWithReplies,
                ParentReply.of(ReplyId.of(replyWithChildrenId)),
                seedReplyMessage("reply_without_children")
        );

        log.info("STG fixed seed scenario prepared.");
    }

    private UserSeed createOrLoadUser(final String seedKey, final String username, final int birthDay) {
        final String email = seedEmail(seedKey);

        if (seedAdminMapper.findUserIdByEmail(email).isEmpty()) {
            userCommandService.createUser(
                    CreateUserCommand.of(
                            UserName.of(username),
                            EmailAddress.of(email),
                            RawPassword.of(properties.getTestPassword()),
                            BirthDate.of(LocalDate.of(1990, 1, birthDay))
                    )
            );
            log.info("Seed user created. email={}, username={}", email, username);
        }

        final String userId = seedAdminMapper.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed user not found after create. email=" + email));

        return new UserSeed(UserId.of(userId), email, username);
    }

    private String createOrLoadPost(final UserSeed author, final String caption, final String sourceObjectKey) {
        if (seedAdminMapper.findPostIdByCaption(caption).isEmpty()) {
            createPost(author.userId(), caption, sourceObjectKey);
            log.info("Seed post created. email={}, caption={}, sourceObjectKey={}", author.email(), caption, sourceObjectKey);
        }

        return seedAdminMapper.findPostIdByCaption(caption)
                .orElseThrow(() -> new IllegalStateException("Seed post not found after create. caption=" + caption));
    }

    private String createOrLoadReply(
            final String postId,
            final UserSeed replier,
            final ParentReply parentReply,
            final String message
    ) {
        if (!seedAdminMapper.existsReplyByPostIdAndMessage(postId, message)) {
            replyCommandService.createReply(
                    CreateReplyCommand.of(
                            PostId.of(postId),
                            replier.userId(),
                            parentReply,
                            Comment.of(message)
                    )
            );
            log.info("Seed reply created. postId={}, replier={}, message={}", postId, replier.email(), message);
        }

        return seedAdminMapper.findReplyIdByPostIdAndMessage(postId, message)
                .orElseThrow(() -> new IllegalStateException("Seed reply not found after create. postId=" + postId));
    }

    private String selectObjectKey(final List<String> imageObjectKeys, final int preferredIndex) {
        if (imageObjectKeys.isEmpty()) {
            throw new IllegalStateException("STG seed requires at least one source image entry.");
        }
        return imageObjectKeys.get(Math.min(preferredIndex, imageObjectKeys.size() - 1));
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
     * @param seedKey seed シナリオ識別子
     * @return メールアドレス
     */
    private String seedEmail(final String seedKey) {
        return properties.getEmailPrefix() + seedKey + "@" + USER_EMAIL_DOMAIN;
    }

    /**
     * seed 用投稿本文を生成する。
     *
     * @param scenarioKey シナリオ識別子
     * @return 投稿本文
     */
    private String seedCaption(final String scenarioKey) {
        return STG_LABEL + " " + scenarioKey;
    }

    /**
     * seed 用返信メッセージを生成する。
     *
     * @param scenarioKey シナリオ識別子
     * @return 返信メッセージ
     */
    private String seedReplyMessage(final String scenarioKey) {
        return STG_LABEL + " " + scenarioKey;
    }

    /**
     * seed 作成対象ユーザーの内部保持用レコード。
     *
     * @param userId ユーザー ID
     * @param email メールアドレス
     * @param username ユーザー名
     */
    private record UserSeed(UserId userId, String email, String username) {
    }
}
