package com.cloudshare.server.repository;

import com.cloudshare.server.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author novo
 * @since 2023/10/5
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
