package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ストレージ上のオブジェクトキーを表す値オブジェクトです。
 * <p>
 * S3 やローカルストレージで利用する相対パス形式のキーを表現し、
 * 生成時に長さ、許可文字、パストラバーサル、絶対パス混入などを検証します。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectKey {

    private static final String USER_SEGMENT = "users";
    private static final String POST_SEGMENT = "posts";
    private static final String IMAGE_SEGMENT = "images";

    /**
     * 最大文字数：1024文字
     * <p>
     * S3 キーの最大長 1024 byte を上限とする。
     */
    static int MAX_LENGTH = 1024;

    /**
     * 許可文字パターン
     * 英数 + /._-
     * <p>
     * 例:
     * users/123/posts/456/images/abc123.jpg
     */
    static Pattern ALLOWED_PATTERN = Pattern.compile("^[A-Za-z0-9._/-]+$");

    static String FIELD_NAME = "object_key";

    String value;

    /**
     * 文字列から {@link ObjectKey} を生成します。
     * <p>
     * 前後空白は除去したうえで、長さ、許可文字、危険なパス表現を検証します。
     *
     * @param value オブジェクトキー文字列
     * @return 検証済みの {@link ObjectKey}
     * @throws InvalidValueException 値が null、blank、形式不正、または危険なパス表現を含む場合
     */
    public static ObjectKey of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 正規化
        final String normalized = value.trim();

        // 空文字チェック
        if (normalized.isEmpty()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        // 最大長チェック
        if (normalized.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, normalized, MAX_LENGTH);
        }

        // 絶対パス攻撃の防止チェック
        if (normalized.startsWith("/")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot start with \"/\"");
        }

        // Windows パス混入のチェック
        if (normalized.contains("\\")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \"\\\\\"");
        }

        // 許可文字チェック
        if (!ALLOWED_PATTERN.matcher(normalized).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, normalized, ALLOWED_PATTERN.pattern());
        }

        // パスの正規化
        if (normalized.contains("//")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \"//\"");
        }

        // パストラバーサル攻撃の防止チェック
        final String[] segments = normalized.split("/");
        for (final String segment : segments) {
            if (segment.equals(".") || segment.equals("..")) {
                throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \".\" or \"..\" or empty segment");
            }
        }

        // ディレクトリとして保存禁止のチェック
        if (normalized.endsWith("/")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot end with \"/\"");
        }

        return new ObjectKey(normalized);
    }

    /**
     * 投稿画像保存用のオブジェクトキーを生成します。
     * <p>
     * 生成フォーマットは
     * {@code users/{userId}/posts/{postId}/images/{imageId}.{extension}}
     * です。
     *
     * @param userId ユーザー ID
     * @param postId 投稿ID
     * @param imageId 画像ID
     * @param extension 拡張子
     * @return 投稿画像保存用の {@link ObjectKey}
     * @throws InvalidValueException いずれかの引数が null の場合、または生成結果が {@link #of(String)} の検証を満たさない場合
     */
    public static ObjectKey createPostImageKey(
            final @Nullable UserId userId,
            final @Nullable PostId postId,
            final @Nullable ImageId imageId,
            final @Nullable FileExtension extension
    ) {
        if (Objects.isNull(userId)) {
            throw InvalidValueException.required(UserId.FIELD_NAME);
        }

        if (Objects.isNull(postId)) {
            throw InvalidValueException.required(PostId.FIELD_NAME);
        }

        if (Objects.isNull(imageId)) {
            throw InvalidValueException.required(ImageId.FIELD_NAME);
        }

        if (Objects.isNull(extension)) {
            throw InvalidValueException.required(FileExtension.FIELD_NAME);
        }

        final String key = String.format(
                "%s/%s/%s/%s/%s/%s.%s",
                USER_SEGMENT,
                userId.getValue(),
                POST_SEGMENT,
                postId.getValue(),
                IMAGE_SEGMENT,
                imageId.getValue(),
                extension.getValue()
        );

        return ObjectKey.of(key);
    }
}
