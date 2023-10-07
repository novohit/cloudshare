package com.cloudshare.server.file.model;

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
 * @since 2023/10/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "file")
public class FileDocument extends AbstractFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 64)
    @Comment("文件后缀")
    private String suffix;
}
