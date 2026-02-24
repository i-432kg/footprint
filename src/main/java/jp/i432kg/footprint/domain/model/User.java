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
    LoginId loginId;
    HashedPassword hashedPassword;

    BirthDate birthDate;
    EmailAddress emailAddress;
    boolean isActive;

    boolean isDisabled;
    LocalDateTime disabledAt;
    LocalDateTime createdAt;
}
