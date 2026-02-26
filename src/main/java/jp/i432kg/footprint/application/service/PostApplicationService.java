package jp.i432kg.footprint.application.service;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.ImageFileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostApplicationService {

    private final PostRepository postRepository;

    private final ImageRepository imageRepository;

    @Transactional
    public void createPost(final Post.NewPost newPost, final MultipartFile imageFile) {

        // 1. 画像ファイルを保存する
        final ImageFileName fileName = imageRepository.save(imageFile);

        // 2.EXIFから座標を特定する.座標が特定できなかった場合は位置情報不明とする.
        final Location location = imageRepository.extractGpsLocation(imageFile)
                .orElse(Location.unknown());

        final Post.NewPost postToSave = newPost
                .withImage(fileName)
                .withLocation(location);

        // 3. 投稿情報を保存する
        postRepository.savePost(postToSave);

    }

    @Transactional(readOnly = true)
    public Posts getMyPosts(final UserId userId) {
        return postRepository.findMyPosts(userId);
    }
}
