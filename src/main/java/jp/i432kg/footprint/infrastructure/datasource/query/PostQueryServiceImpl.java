package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.query.PostQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.Coordinate;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.PostQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 投稿の参照サービス実装クラス
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {

    private final PostQueryMapper postQueryMapper;

    @Override
    public List<PostSummary> listRecentPosts(final PostId lastId, final int size) {
        return postQueryMapper.findRecentPosts(lastId, size);
    }

    @Override
    public List<PostSummary> listMyPosts(final UserId userId, final PostId lastId, final int size) {
        return postQueryMapper.findMyPosts(userId, lastId, size);
    }

    @Override
    public List<PostSummary> searchPosts(final SearchKeyword keyword, final PostId lastId, final int size) {
        return postQueryMapper.findPostsByKeyword(keyword, lastId, size);
    }

    @Override
    public List<PostSummary> searchPostsByBBox(
            final Coordinate minLat,
            final Coordinate maxLat,
            final Coordinate minLng,
            final Coordinate maxLng) {
        return postQueryMapper.findPostsByBBox(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public PostSummary getPost(final PostId postId) {
        return findPost(postId).orElseThrow();
    }

    @Override
    public Optional<PostSummary> findPost(final PostId postId) {
        return postQueryMapper.findPostById(postId);
    }
}
