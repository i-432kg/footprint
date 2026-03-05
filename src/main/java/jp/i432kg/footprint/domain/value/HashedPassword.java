package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ハッシュ化済みのパスワードを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashedPassword {

    String value;

    /**
     * ハッシュ化の抽象インターフェース
     */
    @FunctionalInterface
    public interface Hasher {
        String encode(String rawPassword);
    }

    /**
     * ハッシュ化済みパスワード文字列からインスタンスを生成します。
     * 主にDBからの再構築（リポジトリ層）に使用します。
     *
     * @param hashedValue ハッシュ化済みの文字列
     * @return {@link HashedPassword} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static HashedPassword of(final String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("Hashed password value cannot be null or empty");
        }
        return new HashedPassword(hashedValue);
    }

    /**
     * 生パスワードをハッシュ化して {@link HashedPassword} インスタンスを生成します。
     *
     * @param rawPassword 生パスワード
     * @param hasher      ハッシュ化処理の提供者
     * @return 生成されたハッシュ化済みパスワード
     */
    public static HashedPassword from(final RawPassword rawPassword, final Hasher hasher) {
        return of(hasher.encode(rawPassword.value()));
    }

    public String value() {
        return value;
    }
}