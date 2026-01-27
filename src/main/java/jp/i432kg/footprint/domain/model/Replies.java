package jp.i432kg.footprint.domain.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor(staticName="of")
public class Replies {

    List<Reply> replies;

    public List<Reply> asList() {
        return List.copyOf(replies);
    }
}
