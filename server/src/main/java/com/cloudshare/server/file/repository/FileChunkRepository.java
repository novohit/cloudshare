package com.cloudshare.server.file.repository;

import com.cloudshare.server.file.model.FileChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/13
 */
public interface FileChunkRepository extends JpaRepository<FileChunk, Long> {
    List<FileChunk> findByMd5AndUserIdAndDeletedAtIsNull(String md5, Long userId);
}
