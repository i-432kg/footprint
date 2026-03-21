package jp.i432kg.footprint.application.command;

import com.drew.imaging.ImageProcessingException;
import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
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

    private final ImageRepository imageRepository;

    private final PostDomainService postDomainService;

    /**
     * 新しい投稿を作成します。
     * 画像ファイルの保存、メタデータの抽出、および投稿情報の永続化を一連のトランザクションとして実行します。
     *
     * @param command 投稿作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createPost(final CreatePostCommand command) {

        // 投稿作成時のバリデーション
        postDomainService.validateCreatePost(command.getUserId());

        // PostId を生成 (ULID)
        final PostId postId = PostId.of(UlidCreator.getUlid().toString());

        final StorageObject storageObject;
        try {
            // 画像ファイルを物理ストレージに保存する
            storageObject =
                    imageRepository.save(
                            command.getImageStream(),
                            command.getOriginalFilename(),
                            command.getUserId(),
                            postId
                    );
        } catch (IOException e) {
            throw PostCommandFailedException.imageSaveFailed(
                    command.getOriginalFilename().value(),
                    e
            );
        }

        final Image image;
        try {
            // 保存された実ファイルからメタ情報を抽出して Image ドメインモデルを生成する
            image = imageRepository.extractImageMetadata(storageObject);
        } catch (ImageProcessingException | IOException e) {
            throw PostCommandFailedException.imageMetadataExtractFailed(
                    storageObject.getObjectKey().getValue(),
                    e
            );
        }

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
                    postId.value(),
                    e
            );
        }
    }

}