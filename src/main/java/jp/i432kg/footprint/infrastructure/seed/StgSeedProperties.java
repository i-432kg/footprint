package jp.i432kg.footprint.infrastructure.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * STG seed 実行に関する設定値。
 * <p>
 * {@code app.seed.*} の設定をまとめて保持する。
 * </p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed")
public class StgSeedProperties {

    /**
     * seed を実行するかどうか
     */
    private boolean enabled = false;

    /**
     * seed データの削除のみを行うかどうか
     */
    private boolean cleanupOnly = false;

    /**
     * 実行前に既存 seed データを削除するかどうか
     */
    private boolean cleanupBeforeSeed = false;

    /**
     * seed ユーザーの共通パスワード
     */
    private String testPassword = "Test1234!";

    /**
     * 投稿・返信ありユーザー数
     */
    private int activeUserCount = 2;

    /**
     * 投稿・返信なしユーザー数
     */
    private int inactiveUserCount = 1;

    /**
     * seed 判定用メール接頭辞
     */
    private String emailPrefix = "stg_seed_user_";

    /**
     * seed の元画像を配置した S3 バケット名
     */
    private String sourceBucketName;

    /**
     * seed 画像マニフェストの S3 オブジェクトキー
     */
    private String manifestObjectKey = "seed-source/manifest/seed-images.json";

}
