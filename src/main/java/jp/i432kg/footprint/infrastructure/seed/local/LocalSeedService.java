package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.infrastructure.seed.shared.AbstractFixedSeedService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * local 環境向けの固定シナリオ seed データを投入するサービスです。
 * <p>
 * 投稿あり/なし、返信あり/なしの状態を確認できる最小データセットを維持します。
 * </p>
 */
@Service
@Profile("local")
public class LocalSeedService extends AbstractFixedSeedService {

    private static final String LOCAL_LABEL = "[LOCAL]";

    public LocalSeedService(
            final LocalSeedProperties properties,
            final UserCommandService userCommandService,
            final PostCommandService postCommandService,
            final ReplyCommandService replyCommandService,
            final LocalSeedAdminMapper localSeedAdminMapper,
            final LocalSeedSourceImageProvider seedSourceImageProvider,
            final LocalSeedImageManifestLoader localSeedImageManifestLoader
    ) {
        super(
                properties.getEmailPrefix(),
                properties.getTestPassword(),
                userCommandService,
                postCommandService,
                replyCommandService,
                localSeedAdminMapper,
                seedSourceImageProvider,
                localSeedImageManifestLoader
        );
    }

    /**
     * local 固定シナリオの seed ユーザー、投稿、返信を投入します。
     */
    @Override
    @Transactional
    public void seed() {
        super.seed();
    }

    @Override
    protected String envPrefix() {
        return "loc";
    }

    @Override
    protected String seedLabel() {
        return LOCAL_LABEL;
    }

    @Override
    protected String seedLogName() {
        return "Local";
    }

    @Override
    protected String missingImageEntriesMessage() {
        return "Local seed requires at least one source image.";
    }

    @Override
    protected String imageEntryName() {
        return "sourcePath";
    }
}
