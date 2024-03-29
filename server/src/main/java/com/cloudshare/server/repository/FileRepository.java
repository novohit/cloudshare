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

    List<FileDocument> findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNotNull(String curDirectory, Long userId);

    List<FileDocument> findByUserIdAndCurDirectoryAndNameStartsWithAndDeletedAtIsNull(Long userId, String curDirectory, String key);

    Optional<FileDocument> findByUserIdAndCurDirectoryAndNameAndDeletedAtIsNull(Long userId, String curDirectory, String name);

    List<FileDocument> findByMd5AndDeletedAtIsNull(String md5);

    Optional<FileDocument> findByFileIdAndUserIdAndDeletedAtIsNull(Long fileId, Long userId);

    List<FileDocument> findByParentIdAndUserIdAndDeletedAtIsNull(Long parentId, Long userId);

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

    List<FileDocument> findByUserIdAndCurDirectoryAndDeletedAtIsNotNull(Long userId, String curDirectory);

//    interface StatsProjection {
//        long getDir();
//        long getArchive();
//        long getExcel();
//        long getWord();
//        long getPpt();
//        long getPdf();
//        long getTxt();
//        long getMd();
//        long getCsv();
//        long getImage();
//        long getAudio();
//        long getVideo();
//        long getCode();
//        long getUnknown();
//    }
//
//    @Query(value = "SELECT\n" +
//            "    SUM( CASE WHEN type = 'DIR' THEN 1 ELSE 0 END ) AS dir,\n" +
//            "    SUM( CASE WHEN type = 'ARCHIVE' THEN 1 ELSE 0 END ) AS archive,\n" +
//            "    SUM( CASE WHEN type = 'EXCEL' THEN 1 ELSE 0 END ) AS excel,\n" +
//            "    SUM( CASE WHEN type = 'WORD' THEN 1 ELSE 0 END ) AS word,\n" +
//            "    SUM( CASE WHEN type = 'PPT' THEN 1 ELSE 0 END ) AS ppt,\n" +
//            "    SUM( CASE WHEN type = 'PDF' THEN 1 ELSE 0 END ) AS pdf,\n" +
//            "    SUM( CASE WHEN type = 'TXT' THEN 1 ELSE 0 END ) AS txt,\n" +
//            "    SUM( CASE WHEN type = 'MD' THEN 1 ELSE 0 END ) AS md,\n" +
//            "    SUM( CASE WHEN type = 'CSV' THEN 1 ELSE 0 END ) AS csv,\n" +
//            "    SUM( CASE WHEN type = 'IMAGE' THEN 1 ELSE 0 END ) AS image,\n" +
//            "    SUM( CASE WHEN type = 'AUDIO' THEN 1 ELSE 0 END ) AS audio,\n" +
//            "    SUM( CASE WHEN type = 'VIDEO' THEN 1 ELSE 0 END ) AS video,\n" +
//            "    SUM( CASE WHEN type = 'SOURCE_CODE' THEN 1 ELSE 0 END ) AS code,\n" +
//            "    SUM( CASE WHEN type = 'UNKNOWN' THEN 1 ELSE 0 END ) AS 'unknown'\n" +
//            "FROM\n" +
//            "    file\n" +
//            "WHERE\n" +
//            "    user_id = ?1\n" +
//            "  " +
//            "AND deleted_at IS NULL;", nativeQuery = true)
//    StatsProjection statsCount(Long userId);

    interface StatsProjection {
        String getType();

        Integer getValue();
    }

    @Query(value = "SELECT " +
            "type, " +
            "COUNT(type) AS value\n" +
            "FROM " +
            "file\n" +
            "WHERE " +
            "user_id = ?1 AND deleted_at IS NULL\n" +
            "GROUP BY " +
            "type", nativeQuery = true)
    List<StatsProjection> statsCount(Long userId);

    @Query(value = "SELECT " +
            "type, " +
            "SUM(size) AS value\n" +
            "FROM " +
            "file\n" +
            "WHERE " +
            "user_id = ?1 AND deleted_at IS NULL\n" +
            "GROUP BY " +
            "type", nativeQuery = true)
    List<StatsProjection> statsSize(Long userId);
}
