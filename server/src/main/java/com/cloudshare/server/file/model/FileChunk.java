package com.cloudshare.server.file.model;

import com.cloudshare.server.common.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author novo
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "file_chunk")
public class FileChunk extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 64, nullable = false)
    @Comment("逻辑名称")
    private String name;

    @Column(nullable = false)
    @Comment("分片序号")
    private Integer chunkNum;

    @Column(nullable = false)
    @Comment("分片大小")
    private Long chunkSize;

    @Column(length = 256)
    @Comment("MD5")
    private String md5;

    @Column(length = 256)
    @Comment("分片合并所需的信息")
    private String chunkInfo;
}
