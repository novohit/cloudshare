package com.cloudshare.server.user.repository;

import com.cloudshare.server.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/5
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
