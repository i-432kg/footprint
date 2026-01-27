package jp.i432kg.footprint.domain.value;

public record ReplyId(int value) {

    public static ReplyId valueOf(String value) {
        return new ReplyId(Integer.parseInt(value));
    }
}
