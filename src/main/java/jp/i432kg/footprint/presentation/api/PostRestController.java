package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jp.i432kg.footprint.application.service.PostApplicationService;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.datasource.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.PostRequest;
import jp.i432kg.footprint.presentation.api.dto.PostResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
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
                .userId(userDetails.getUser().getId())
                .comment(new Comment(request.getComment()))
                .build();

        postApplicationService.createPost(newPost, request.getImageFile());

        return "redirect:/";
    }
}
