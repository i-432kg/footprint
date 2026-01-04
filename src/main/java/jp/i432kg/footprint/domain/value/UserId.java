package jp.i432kg.footprint.domain.value;

public record UserId(int value) {

    static final int MAX_SIZE = 9999;
    static final int MIN_SIZE = 0;

    public UserId {

//        if (value < MIN_SIZE)
//            throw new IllegalArgumentException("UserId is too small :" + value);
//
//        if (value > MAX_SIZE)
//            throw new IllegalArgumentException("UserId is too big :" + value);
    }
}
