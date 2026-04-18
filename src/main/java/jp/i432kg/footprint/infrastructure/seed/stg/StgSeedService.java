package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.application.command.service.PostCommandService;
import jp.i432kg.footprint.application.command.service.ReplyCommandService;
import jp.i432kg.footprint.application.command.service.UserCommandService;
import jp.i432kg.footprint.infrastructure.seed.shared.AbstractFixedSeedService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * STG 環境向けの固定シナリオ seed データを投入するサービスです。
 * <p>
 * 投稿あり/なし、返信あり/なしの状態を確認できる最小データセットを維持します。
 * </p>
 */
@Service
@Profile("stg")
public class StgSeedService extends AbstractFixedSeedService {

    private static final String STG_LABEL = "[STG]";

    public StgSeedService(
            final StgSeedProperties properties,
            final UserCommandService userCommandService,
            final PostCommandService postCommandService,
            final ReplyCommandService replyCommandService,
            final StgSeedAdminMapper seedAdminMapper,
            final S3SeedSourceImageProvider seedSourceImageProvider,
            final StgSeedImageManifestLoader seedImageManifestLoader,
            final Clock clock
    ) {
        super(
                properties.getEmailPrefix(),
                properties.getTestPassword(),
                userCommandService,
                postCommandService,
                replyCommandService,
                seedAdminMapper,
                seedSourceImageProvider,
                seedImageManifestLoader,
                clock
        );
    }

    /**
     * STG 固定シナリオの seed ユーザー、投稿、返信を投入します。
     */
    @Override
    @Transactional
    public void seed() {
        super.seed();
    }

    @Override
    protected String envPrefix() {
        return "stg";
    }

    @Override
    protected String seedLabel() {
        return STG_LABEL;
    }

    @Override
    protected String seedLogName() {
        return "STG";
    }

    @Override
    protected String missingImageEntriesMessage() {
        return "STG seed requires at least one source image entry.";
    }

    @Override
    protected String imageEntryName() {
        return "sourceObjectKey";
    }
}
