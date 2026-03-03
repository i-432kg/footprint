package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 投稿に関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;

    private final ImageRepository imageRepository;

    /**
     * 新しい投稿を作成します。
     * 画像ファイルの保存、メタデータの抽出、および投稿情報の永続化を一連のトランザクションとして実行します。
     *
     * @param command 投稿作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createPost(final CreatePostCommand command) {

        // 画像ファイルを物理ストレージに保存する
        final FileName savedFileName =
                imageRepository.save(command.getImageStream(), command.getOriginalFilename());
        final FilePath filePath = FilePath.of(savedFileName.value());

        // 保存された実ファイルからメタ情報を抽出して Image ドメインモデルを生成する
        final Image image = imageRepository.extractImageMetadata(filePath);

        // Post ドメインモデルを構築し、DBに永続化する
        final Post post = Post.of(
                command.getUserId(),
                image,
                command.getComment(),
                LocalDateTime.now()
        );

        postRepository.savePost(post);
    }

}
