package com.cloudshare.server.link.repository;

import com.cloudshare.server.link.model.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author novo
 * @since 2023/11/10
 */
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
    ShortLink findOneByCode(String code);
}
