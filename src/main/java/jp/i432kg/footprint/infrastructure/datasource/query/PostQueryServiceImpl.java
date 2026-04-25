package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.query.service.PostQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.PostQueryMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link PostQueryService} のデータソース参照実装。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostQueryMapper postQueryMapper;

    @Override
    public List<PostSummary> listRecentPosts(final @Nullable PostId lastId, final int size) {
        return Objects.isNull(lastId)
                ? postQueryMapper.findRecentPostsFirstPage(size)
                : postQueryMapper.findRecentPostsAfterCursor(lastId, size);
    }

    @Override
    public List<PostSummary> listMyPosts(final UserId userId, final @Nullable PostId lastId, final int size) {
        return Objects.isNull(lastId)
                ? postQueryMapper.findMyPostsFirstPage(userId, size)
                : postQueryMapper.findMyPostsAfterCursor(userId, lastId, size);
    }

    @Override
    public List<PostSummary> searchPosts(final SearchKeyword keyword, final @Nullable PostId lastId, final int size) {
        return Objects.isNull(lastId)
                ? postQueryMapper.findPostsByKeywordFirstPage(keyword, size)
                : postQueryMapper.findPostsByKeywordAfterCursor(keyword, lastId, size);
    }

    @Override
    public List<PostSummary> searchPostsByBBox(
            final Latitude minLat,
            final Latitude maxLat,
            final Longitude minLng,
            final Longitude maxLng
    ) {
        return postQueryMapper.findPostsByBBox(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public PostSummary getPost(final PostId postId) throws PostNotFoundException {
        return findPost(postId).orElseThrow(() -> new PostNotFoundException(postId));
    }

    @Override
    public Optional<PostSummary> findPost(final PostId postId) {
        return postQueryMapper.findPostById(postId);
    }
}
