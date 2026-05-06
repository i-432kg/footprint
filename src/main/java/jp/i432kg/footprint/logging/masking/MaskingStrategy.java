package jp.i432kg.footprint.logging.masking;

/**
 * 機微情報のマスク方法を表します。
 */
public enum MaskingStrategy {

    /**
     * 値全体を固定文字列で置き換えます。
     */
    FULL {
        @Override
        public Object mask(final Object value) {
            return MASKED_VALUE;
        }
    },

    /**
     * メールアドレスのローカル部を部分的にマスクします。
     */
    EMAIL {
        @Override
        public Object mask(final Object value) {
            final String email = value.toString();
            final int atIndex = email.indexOf('@');
            if (atIndex <= 1) {
                return MASKED_VALUE;
            }
            return email.charAt(0) + MASKED_VALUE + email.substring(atIndex);
        }
    };

    private static final String MASKED_VALUE = "********";

    /**
     * 指定された値を、この戦略に従ってマスクします。
     *
     * @param value マスク対象の値
     * @return マスク後の値
     */
    public abstract Object mask(Object value);
}
