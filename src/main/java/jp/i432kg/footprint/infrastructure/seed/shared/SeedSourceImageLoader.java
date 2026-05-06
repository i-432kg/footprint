package jp.i432kg.footprint.infrastructure.seed.shared;

/**
 * seed 投稿に利用する元画像を取得する契約です。
 */
public interface SeedSourceImageLoader {

    /**
     * 指定エントリに対応する seed 用元画像を返します。
     *
     * @param entry 画像取得元を表すエントリ
     * @return seed 用元画像
     */
    SeedSourceImage load(String entry);
}
