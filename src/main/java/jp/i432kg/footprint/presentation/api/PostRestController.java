package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jp.i432kg.footprint.application.service.PostApplicationService;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.PostRequest;
import jp.i432kg.footprint.presentation.api.dto.PostResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostApplicationService postApplicationService;
    private final ImageUrlConverter imageUrlConverter;

    // 投稿一覧の取得
    @GetMapping
    public ResponseEntity<List<PostResponse>> getRecentPosts(
            @RequestParam(required = false) PostId lastId,
            @RequestParam(defaultValue = "10") int size) {

        Posts posts = postApplicationService.getRecentPosts(lastId, size);

        List<PostResponse> responses = posts.asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())))
                .toList();

        return ResponseEntity.ok(responses);
    }

    // 検索した投稿一覧を取得
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> search(
            @RequestParam final SearchKeyword keyword,
            @RequestParam(required = false) final PostId lastId,
            @RequestParam(defaultValue = "10") int size) {

        Posts posts = postApplicationService.searchPosts(keyword, lastId, size);

        List<PostResponse> responses = posts.asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())))
                .toList();

        return ResponseEntity.ok(responses);
    }

    // 投稿詳細の取得
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable final PostId postId) {
        Post post = postApplicationService.getPostDetail(postId);
        return ResponseEntity.ok(new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())));
    }

    // 投稿を作成
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid final PostRequest request,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {

        final Post.NewPost newPost = Post.newPost()
                .userId(userDetails.getUserId())
                .comment(new Comment(request.getComment()))
                .build();

        postApplicationService.createPost(newPost, request.getImageFile());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
