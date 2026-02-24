package jp.i432kg.footprint.presentation.api.dto;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.Coordinate;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PostResponse {

    Integer id;
    String imageUrl;
    String comment;
    Double latitude;
    Double longitude;
    LocalDateTime createdAt;

    public PostResponse(final Post post, final String imageUrl) {
        this.id = post.getId().value();
        this.imageUrl = imageUrl;
        this.comment = post.getCaption().value();

        final Location loc = post.getLocation();
        this.latitude = loc.getLatitude().map(Coordinate::value).orElse(null);
        this.longitude = loc.getLongitude().map(Coordinate::value).orElse(null);

        this.createdAt = post.getCreatedAt();
    }

}
