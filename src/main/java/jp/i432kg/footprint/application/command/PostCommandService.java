package jp.i432kg.footprint.application.command;

import com.drew.imaging.ImageProcessingException;
import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.application.port.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.ImageStorage;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 投稿に関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;

    private final ImageStorage imageStorage;

    private final ImageMetadataExtractor imageMetadataExtractor;

    private final UserDomainService userDomainService;

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
        final PostId postId = PostId.of(UlidCreator.getUlid().toString());

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
            throw PostCommandFailedException.imageSaveFailed(
                    command.getOriginalFilename().getValue(),
                    e
            );
        }

        final ImageMetadata imageMetadata;
        try {
            // 保存された実ファイルからメタ情報を抽出する
            imageMetadata = imageMetadataExtractor.extract(storageObject);
        } catch (ImageProcessingException | IOException e) {
            throw PostCommandFailedException.imageMetadataExtractFailed(
                    storageObject.getObjectKey().getValue(),
                    e
            );
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
                LocalDateTime.now()
        );

        try {
            postRepository.savePost(post);
        } catch (DataAccessException e) {
            throw PostCommandFailedException.persistenceFailed(
                    postId.getValue(),
                    e
            );
        }
    }

}
