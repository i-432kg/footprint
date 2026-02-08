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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final PostApplicationService postApplicationService;
    private final ImageUrlConverter imageUrlConverter;

    @GetMapping("/api/post/{postId}")
    public PostResponse getPost(@PathVariable PostId postId){
        Post post = postApplicationService.getPostDetail(postId);
        return new PostResponse(post, imageUrlConverter.convert(post.getImageFileName()));
    }

    @GetMapping("/api/posts")
    public List<PostResponse> getPosts() {

        Posts posts = postApplicationService.getRecentPosts();

        return posts.asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())))
                .toList();
    }

    @PostMapping("/api/post")
    public String post(@Valid final PostRequest request, @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        final Post.NewPost newPost = Post.newPost()
                .userId(userDetails.getUserId())
                .comment(new Comment(request.getComment()))
                .build();

        postApplicationService.createPost(newPost, request.getImageFile());

        return "redirect:/";
    }

    @GetMapping("/api/posts/search")
    public ResponseEntity<List<PostResponse>> search(
            @RequestParam SearchKeyword keyword,
            @RequestParam(required = false) PostId lastId,
            @RequestParam(defaultValue = "10") int size) {

        List<PostResponse> responses = postApplicationService.searchPosts(keyword, lastId, size)
                .asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())))
                .toList();

        return ResponseEntity.ok(responses);
    }
}
