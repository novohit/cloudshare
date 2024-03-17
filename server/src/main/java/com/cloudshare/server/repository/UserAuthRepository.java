package com.cloudshare.server.repository;

import com.cloudshare.server.enums.LoginType;
import com.cloudshare.server.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/6
 */
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByLoginTypeAndIdentify(LoginType loginType, String identify);
}
