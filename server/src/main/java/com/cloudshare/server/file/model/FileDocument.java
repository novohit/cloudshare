package com.cloudshare.server.file.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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
})
public class FileDocument extends AbstractFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


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
