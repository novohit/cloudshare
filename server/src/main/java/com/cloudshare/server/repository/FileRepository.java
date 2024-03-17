package com.cloudshare.server.repository;

import com.cloudshare.server.enums.FileType;
import com.cloudshare.server.model.FileDocument;
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

    List<FileDocument> findByNameContainingAndCurDirectoryAndUserIdAndDeletedAtIsNull(String key, String curDirectory, Long userId);

    List<FileDocument> findByContentContainingAndCurDirectoryAndUserIdAndDeletedAtIsNull(String key, String curDirectory, Long userId);

    List<FileDocument> findByUserIdAndCurDirectoryAndDeletedAtIsNull(Long userId, String curDirectory);

    List<FileDocument> findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNull(String curDirectory, Long userId);

    List<FileDocument> findByUserIdAndCurDirectoryAndNameStartsWithAndDeletedAtIsNull(Long userId, String curDirectory, String key);

    Optional<FileDocument> findByUserIdAndCurDirectoryAndNameAndDeletedAtIsNull(Long userId, String curDirectory, String name);

    List<FileDocument> findByMd5AndDeletedAtIsNull(String md5);

    Optional<FileDocument> findByFileIdAndUserIdAndDeletedAtIsNull(Long fileId, Long userId);

    List<FileDocument> findByFileIdInAndUserId(List<Long> fileIds, Long userId);

    List<FileDocument> findByUserIdAndTypeAndDeletedAtIsNull(Long userId, FileType type);

    @Modifying
    @Query("UPDATE file AS f " +
            "SET f.name = :name, " +
            "f.description = :description " +
            "WHERE f.fileId = :fileId AND f.userId = :userId")
    int updateDirByFileIdAndUserId(@Param("fileId") Long fileId,
                                   @Param("userId") Long userId,
                                   @Param("name") String name,
                                   @Param("description") String description);

    @Modifying
    @Query("UPDATE file AS f " +
            "SET f.name = :newName, " +
            "f.path = :newPath " +
            "WHERE f.fileId = :fileId AND f.userId = :userId AND f.name = :oldName")
    int renameDir(@Param("fileId") Long fileId,
                  @Param("userId") Long userId,
                  @Param("oldName") String oldName,
                  @Param("newName") String newName,
                  @Param("newPath") String newPath);

    List<FileDocument> findByUserIdAndDeletedAtIsNull(Long userId);
}
