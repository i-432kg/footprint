package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.query.service.UserQueryService;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.UserQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * {@link UserQueryService} のデータソース参照実装。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserQueryMapper userQueryMapper;

    @Override
    public UserProfileSummary getUserProfile(final UserId userId) throws UserNotFoundException {
        return findUserProfile(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public Optional<UserProfileSummary> findUserProfile(final UserId userId) {
        return userQueryMapper.findProfileByUserId(userId);
    }
}
