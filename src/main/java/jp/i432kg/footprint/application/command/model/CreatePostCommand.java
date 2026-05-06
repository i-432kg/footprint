package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostComment;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

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
    UserId userId;

    /**
     * 投稿本文
     */
    PostComment comment;

    /**
     * 投稿された画像データの入力ストリーム
     */
    InputStream imageStream;

    /**
     * アップロードされた画像の元のファイル名
     */
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
            final UserId userId,
            final PostComment comment,
            final InputStream imageStream,
            final FileName originalFilename
    ) {
        return new CreatePostCommand(userId, comment, imageStream, originalFilename);
    }
}
