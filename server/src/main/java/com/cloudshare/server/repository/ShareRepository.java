package com.cloudshare.server.repository;

import com.cloudshare.server.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/30
 */
public interface ShareRepository extends JpaRepository<Share, Long> {

    List<Share> findByUserId(Long userId);

    Optional<Share> findByShareIdAndUserId(Long shareId, Long userId);

    Optional<Share> findByShareId(Long shareId);

    List<Share> findByShareIdInAndUserId(List<Long> ids, Long userId);
}
