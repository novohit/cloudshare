package com.cloudshare.server.file.repository;

import com.cloudshare.server.file.model.FileDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author novo
 * @since 2023/10/8
 */
public interface FileRepository extends JpaRepository<FileDocument, Long> {

    List<FileDocument> findByUserIdAndCurDirectory(Long userId, String curDirectory);

    List<FileDocument> findByUserIdAndCurDirectoryAndNameStartsWith(Long userId, String curDirectory, String key);

    Optional<FileDocument> findByUserIdAndCurDirectoryAndName(Long userId, String curDirectory, String name);

    @Modifying
    @Query("UPDATE file AS f " +
            "SET f.name = :name, " +
            "f.description = :description " +
            "WHERE f.id = :id AND f.userId = :userId")
    int updateDirByIdAndUserId(@Param("id") Long id,
                               @Param("userId") Long userId,
                               @Param("name") String name,
                               @Param("description") String description);

    @Modifying
    @Query("UPDATE file AS f " +
            "SET f.name = :newName, " +
            "f.path = :newPath " +
            "WHERE f.id = :id AND f.userId = :userId AND f.name = :oldName")
    int renameDir(@Param("id") Long id,
                  @Param("userId") Long userId,
                  @Param("oldName") String oldName,
                  @Param("newName") String newName,
                  @Param("newPath") String newPath);
}
