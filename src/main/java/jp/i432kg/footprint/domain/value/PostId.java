package jp.i432kg.footprint.domain.value;

public record PostId(int value) {

    public static PostId valueOf(String value) {
        return new PostId(Integer.parseInt(value));
    }
}
