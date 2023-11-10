package org.novo.datasync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "file", createIndex = true)
public class FileDoc implements Serializable {

    @Serial
    private static final long serialVersionUID = 2906954784128153248L;

    @Id
    private Long id;

    private Long fileId;

    private Long userId;

    private Long parentId;

    private String path;

    private String curDirectory;

    private String realPath;

    private String type;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word")
    private String name;

    private Long size;

    private String description;

    private String suffix;

    private String realName;

    private String md5;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
