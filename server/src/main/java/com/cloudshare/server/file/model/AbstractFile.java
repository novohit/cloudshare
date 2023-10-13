package com.cloudshare.server.file.model;

import com.cloudshare.server.BaseModel;
import com.cloudshare.server.file.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * TODO 后续考虑用 mongo 存
 *
 * @author novo
 * @since 2023/10/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class AbstractFile extends BaseModel {

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
}
