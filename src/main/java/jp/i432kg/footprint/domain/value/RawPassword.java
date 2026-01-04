package jp.i432kg.footprint.domain.value;

public record RawPassword(String value) {

    static final int MAX_LENGTH = 128;
    static final int MIN_LENGTH = 15;

    public RawPassword {

//        if (value.length() < MIN_LENGTH)
//            throw new IllegalArgumentException("Password is too short :" + value.length());
//
//        if (value.length() > MAX_LENGTH)
//            throw new IllegalArgumentException("Password is too long :" + value.length());
    }
}
