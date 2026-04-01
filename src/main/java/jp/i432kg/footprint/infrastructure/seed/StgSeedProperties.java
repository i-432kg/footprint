package jp.i432kg.footprint.infrastructure.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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
     * seed で作成するユーザー数
     */
    private int userCount = 3;

    /**
     * 1ユーザーあたりの投稿数
     */
    private int postsPerUser = 4;

    /**
     * seed 判定用メール接頭辞
     */
    private String emailPrefix = "stg_seed_user_";

    /**
     * seed の元画像を配置した S3 バケット名
     */
    private String sourceBucketName;

    /**
     * seed の元画像オブジェクトキー一覧
     */
    private List<String> sourceImageObjectKeys = new ArrayList<>();
}