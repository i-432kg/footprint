package jp.i432kg.footprint.presentation.validation;

/**
 * presentation 層の Bean Validation で利用する正規表現定義です。
 */
public final class PresentationValidationPatterns {

    /**
     * ULID の形式
     */
    public static final String ULID = "^[0-9A-HJKMNP-TV-Z]{26}$";

    /**
     * CR/LF を除く制御文字を禁止する形式
     */
    public static final String NO_CONTROL_CHARS = "^(?!.*[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]).*$";

    /**
     * 空白を含まない ASCII 可視文字のみを許可する形式
     */
    public static final String ASCII_VISIBLE_NO_SPACE = "^[\\x21-\\x7E]+$";

    private PresentationValidationPatterns() {
    }
}
