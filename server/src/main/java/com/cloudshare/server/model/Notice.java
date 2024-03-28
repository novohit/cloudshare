package com.cloudshare.server.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.enums.PlanLevel;
import com.cloudshare.server.enums.RoleEnum;
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


/**
 * @author novo
 * @since 2023/10/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "notice")
public class Notice extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 256)
    private String title;

    private String content;
}
