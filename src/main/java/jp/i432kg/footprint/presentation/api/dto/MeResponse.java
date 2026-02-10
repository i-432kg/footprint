package jp.i432kg.footprint.presentation.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeResponse {

    String name;

    boolean isAuthenticated;

    public static MeResponse of(final String name, final boolean isAuthenticated) {
        return new MeResponse(name, isAuthenticated);
    }
}
