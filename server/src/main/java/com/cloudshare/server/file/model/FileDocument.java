package com.cloudshare.server.file.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.file.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author novo
 * @since 2023/10/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "file")
@Table(indexes = {
        @Index(name = "idx_md5", columnList = "md5"),
        @Index(name = "idx_cur_directory_user_id", columnList = "curDirectory,userId"),
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_path", columnNames = {"path"})
})
public class FileDocument extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 2906954784128153248L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fileId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Comment("父级ID 冗余字段 暂时不会用到")
    private Long parentId;

    @Column(length = 256, nullable = false)
    @Comment("文件全路径")
    private String path;

    @Column(length = 256, nullable = false)
    @Comment("当前目录")
    private String curDirectory;

    @Column(length = 256)
    @Comment("真实路径")
    private String realPath;

    @Column(length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("文件类型")
    private FileType type;

    @Column(length = 64, nullable = false)
    @Comment("文件/目录 逻辑名称")
    private String name;

    @Column(nullable = false)
    @Comment("文件大小")
    private Long size;

    @Column(length = 256)
    @Comment("描述")
    private String description;

    // ================
    @Column(length = 64)
    @Comment("文件后缀")
    private String suffix;

    @Column(length = 64)
    @Comment("真实文件名")
    private String realName;

    @Column(length = 256)
    @Comment("MD5")
    private String md5;
}
