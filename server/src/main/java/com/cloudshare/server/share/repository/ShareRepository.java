package com.cloudshare.server.share.repository;

import com.cloudshare.server.share.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/30
 */
public interface ShareRepository extends JpaRepository<Share, Long> {

    List<Share> findByUserId(Long userId);

    Optional<Share> findByIdAndUserId(Long shareId, Long userId);
}
