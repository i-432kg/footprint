package jp.i432kg.footprint.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jp.i432kg.footprint.domain.value.*;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class User {

    UserId id;
    UserName name;
    HashedPassword hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    Authority authority;

    BirthDate birthDate;

    boolean isDisabled;
    LocalDateTime disabledAt;
    LocalDateTime createdAt;
}
