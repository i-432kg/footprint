package jp.i432kg.footprint.domain.value;

public record UserName(String value) {

    static final int MAX_LENGTH = 30;
    static final int MIN_LENGTH = 4;

    public UserName {

//        if (value.length() < MIN_LENGTH)
//            throw new IllegalArgumentException("UserName is too short :" + value.length());
//
//        if (value.length() > MAX_LENGTH)
//            throw new IllegalArgumentException("UserName is too long :" + value.length());
    }
}
