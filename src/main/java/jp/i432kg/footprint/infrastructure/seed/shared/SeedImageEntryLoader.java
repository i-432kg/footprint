package jp.i432kg.footprint.infrastructure.seed.shared;

import java.util.List;

/**
 * seed 投稿に利用する元画像エントリ一覧を取得する契約です。
 */
public interface SeedImageEntryLoader {

    /**
     * seed 投稿に利用する元画像エントリ一覧を返します。
     *
     * @return 元画像エントリ一覧
     */
    List<String> loadEntries();
}
