package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostComment;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;

/**
 * 投稿作成を指示するコマンドオブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePostCommand {

    /**
     * 投稿者
     */
    @NonNull
    UserId userId;

    /**
     * 投稿本文
     */
    @NonNull
    PostComment comment;

    /**
     * 投稿された画像データの入力ストリーム
     */
    @NonNull
    InputStream imageStream;

    /**
     * アップロードされた画像の元のファイル名
     */
    @NonNull
    FileName originalFilename;


    /**
     * {@link CreatePostCommand} のインスタンスを生成します。
     *
     * @param userId           投稿者のユーザーID
     * @param comment          投稿本文
     * @param imageStream      画像データの入力ストリーム
     * @param originalFilename 元のファイル名
     * @return {@link CreatePostCommand} インスタンス
     */
    public static CreatePostCommand of(
            final @NonNull UserId userId,
            final @NonNull PostComment comment,
            final @NonNull InputStream imageStream,
            final @NonNull FileName originalFilename
    ) {
        return new CreatePostCommand(userId, comment, imageStream, originalFilename);
    }
}
