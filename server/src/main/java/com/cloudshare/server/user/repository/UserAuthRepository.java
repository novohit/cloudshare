package com.cloudshare.server.user.repository;

import com.cloudshare.server.user.enums.LoginType;
import com.cloudshare.server.user.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/6
 */
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByLoginTypeAndIdentify(LoginType loginType, String identify);
}
