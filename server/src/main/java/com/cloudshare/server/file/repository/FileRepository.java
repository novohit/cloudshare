package com.cloudshare.server.file.repository;

import com.cloudshare.server.file.model.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public interface FileRepository extends JpaRepository<FileDocument, Long> {

    List<FileDocument> findByUserIdAndCurDirectory(Long userId, String curDirectory);

    List<FileDocument> findByUserIdAndCurDirectoryAndNameStartsWith(Long userId, String curDirectory, String key);
}
