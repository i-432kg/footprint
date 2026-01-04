package jp.i432kg.footprint.domain.model.user;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jp.i432kg.footprint.domain.value.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    UserId id;
    UserName name;
    HashedPassword hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    Authority authority;

    boolean isDisabled;
    LocalDateTime disabledAt;
    LocalDateTime createdAt;
}
