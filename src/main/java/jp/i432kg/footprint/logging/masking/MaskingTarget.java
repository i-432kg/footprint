package jp.i432kg.footprint.logging.masking;

import java.util.Arrays;

/**
 * キー名やフィールド名から機微情報を識別するための定義です。
 */
public enum MaskingTarget {

    /**
     * パスワードや secret 系の値を表します。
     */
    PASSWORD(MaskingStrategy.FULL, "password", "secret"),

    /**
     * トークンや credential 系の値を表します。
     */
    TOKEN(MaskingStrategy.FULL, "token", "credential"),

    /**
     * メールアドレス系の値を表します。
     */
    EMAIL(MaskingStrategy.EMAIL, "email");

    private final MaskingStrategy strategy;
    private final String[] keywords;

    MaskingTarget(final MaskingStrategy strategy, final String... keywords) {
        this.strategy = strategy;
        this.keywords = keywords;
    }

    /**
     * このターゲットに対応するマスク方法で値を変換します。
     *
     * @param value マスク対象の値
     * @return マスク後の値
     */
    public Object mask(final Object value) {
        return strategy.mask(value);
    }

    /**
     * 指定されたキー名が、このターゲットに該当するかどうかを判定します。
     *
     * @param key 判定対象のキー名
     * @return 該当する場合は true
     */
    public boolean matches(final String key) {
        final String normalized = key.toLowerCase();
        return Arrays.stream(keywords).anyMatch(normalized::contains);
    }
}
