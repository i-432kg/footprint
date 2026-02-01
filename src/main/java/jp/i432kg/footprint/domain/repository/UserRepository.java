package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.UserName;

public interface UserRepository {

    int countUser(UserName userName);

    void saveUser(User user);

}
