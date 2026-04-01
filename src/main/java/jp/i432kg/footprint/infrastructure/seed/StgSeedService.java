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

    @Transactional
    public void seed() {
        final List<UserSeed> users = createSeedUsers();
        createSeedPosts(users);
    }

    private List<UserSeed> createSeedUsers() {
        final List<UserSeed> users = new ArrayList<>();

        for (int i = 1; i <= properties.getUserCount(); i++) {
            final String email = seedEmail(i);
            final String username = seedUsername(i);

            if (seedAdminMapper.findUserIdByEmail(email).isEmpty()) {
                userCommandService.createUser(
                        CreateUserCommand.of(
                                UserName.of(username),
                                EmailAddress.of(email),
                                RawPassword.of(properties.getTestPassword()),
                                BirthDate.of(LocalDate.of(1990, 1, Math.min(i, 28)))
                        )
                );
                log.info("Seed user created. email={}", email);
            }

            final String userId = seedAdminMapper.findUserIdByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Seed user not found after create. email=" + email));

            users.add(new UserSeed(UserId.of(userId), email, username));
        }

        return users;
    }

    private void createSeedPosts(final List<UserSeed> users) {
        for (int userIndex = 0; userIndex < users.size(); userIndex++) {
            final UserSeed author = users.get(userIndex);

            for (int postIndex = 1; postIndex <= properties.getPostsPerUser(); postIndex++) {
                final String caption = seedCaption(author.username(), postIndex);

                if (seedAdminMapper.findPostIdByCaption(caption).isEmpty()) {
                    createPost(author.userId(), caption, resolveImageObjectKey(postIndex));
                    log.info("Seed post created. email={}, caption={}", author.email(), caption);
                }

                final String postId = seedAdminMapper.findPostIdByCaption(caption)
                        .orElseThrow(() -> new IllegalStateException("Seed post not found after create. caption=" + caption));

                if (postIndex % 2 == 0) {
                    final UserSeed replier = users.get((userIndex + 1) % users.size());
                    final String replyMessage = seedReplyMessage(postIndex, replier.username());
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
        }
    }

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

    private String seedEmail(final int number) {
        return properties.getEmailPrefix() + String.format("%02d@%s", number, USER_EMAIL_DOMAIN);
    }

    private String seedUsername(final int number) {
        return String.format("stgseed%02d", number);
    }

    private String seedCaption(final String username, final int postIndex) {
        return String.format("[STG] %s post%02d", username, postIndex);
    }

    private String seedReplyMessage(final int postIndex, final String username) {
        return String.format("[STG] %s reply%02d", username, postIndex);
    }

    private String resolveImageObjectKey(final int postIndex) {
        final List<String> sourceImageObjectKeys = properties.getSourceImageObjectKeys();
        if (sourceImageObjectKeys == null || sourceImageObjectKeys.isEmpty()) {
            throw new IllegalStateException("app.seed.source-image-object-keys must contain at least one object key.");
        }

        final int index = Math.floorMod(postIndex - 1, sourceImageObjectKeys.size());
        return sourceImageObjectKeys.get(index);
    }

    private record UserSeed(UserId userId, String email, String username) {
    }
}
