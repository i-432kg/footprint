package jp.i432kg.footprint.application.command.service;

import com.drew.imaging.ImageProcessingException;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.application.port.storage.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.storage.ImageStorage;
import jp.i432kg.footprint.application.port.id.PostIdGenerator;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.logging.LoggingCategories;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 投稿に関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class PostCommandService {

    private static final String EVENT_POST_CREATE_SUCCESS = "POST_CREATE_SUCCESS";
    private static final Logger APP_LOGGER = LoggerFactory.getLogger(LoggingCategories.APP);
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger(LoggingCategories.AUDIT);

    private final PostRepository postRepository;

    private final ImageStorage imageStorage;

    private final ImageMetadataExtractor imageMetadataExtractor;

    private final UserDomainService userDomainService;

    private final Clock clock;

    private final PostIdGenerator postIdGenerator;

    /**
     * 新しい投稿を作成します。
     * 画像ファイルの保存、メタデータの抽出、および投稿情報の永続化を一連のトランザクションとして実行します。
     *
     * @param command 投稿作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createPost(final CreatePostCommand command) {

        // 投稿したユーザーの存在確認を行う
        if (!userDomainService.isExistUser(command.getUserId())) {
            throw new UserNotFoundException(command.getUserId());
        }

        // PostId を生成 (ULID)
        final PostId postId = postIdGenerator.generate();

        final StorageObject storageObject;
        try {
            // 画像ファイルを物理ストレージに保存する
            storageObject =
                    imageStorage.store(
                            command.getImageStream(),
                            command.getOriginalFilename(),
                            command.getUserId(),
                            postId
                    );
        } catch (IOException e) {
            throw PostCommandFailedException.imageSaveFailed(e);
        }

        final ImageMetadata imageMetadata;
        try {
            // 保存された実ファイルからメタ情報を抽出する
            imageMetadata = imageMetadataExtractor.extract(storageObject);
        } catch (ImageProcessingException | IOException e) {
            cleanupStoredImage(storageObject);
            throw PostCommandFailedException.imageMetadataExtractFailed(e);
        }

        // 抽出したメタデータと保存先情報から Image ドメインモデルを生成する
        final Image image = Image.of(
                storageObject,
                imageMetadata.getFileExtension(),
                imageMetadata.getFileSize(),
                imageMetadata.getWidth(),
                imageMetadata.getHeight(),
                imageMetadata.getLocation(),
                imageMetadata.isHasEXIF(),
                imageMetadata.getTakenAt()
        );

        // Post ドメインモデルを構築し、DBに永続化する
        final Post post = Post.of(
                postId,
                command.getUserId(),
                image,
                command.getComment(),
                LocalDateTime.now(clock)
        );

        try {
            postRepository.savePost(post);
        } catch (DataAccessException e) {
            cleanupStoredImage(storageObject);
            throw PostCommandFailedException.persistenceFailed(e);
        }

        AUDIT_LOGGER.info(
                "event={}, postId={}, userId={}, imageSizeBytes={}, hasLocation={}",
                EVENT_POST_CREATE_SUCCESS,
                post.getPostId().getValue(),
                post.getUserId().getValue(),
                post.getImage().getFileSize().getValue(),
                post.getImage().hasLocation()
        );
    }

    private void cleanupStoredImage(final StorageObject storageObject) {
        try {
            imageStorage.delete(storageObject);
        } catch (IOException e) {
            // ここで cleanup 失敗を再送出すると一次障害の原因が隠れるため、
            // 元例外を優先しつつ補償処理失敗はログに残す。
            APP_LOGGER.warn("Failed to cleanup stored image after post processing failure.", e);
        }
    }

}
