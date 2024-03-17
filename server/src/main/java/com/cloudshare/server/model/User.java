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
@Entity(name = "user")
public class User extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String username;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 64, nullable = false)
    private String salt;

    @Column(length = 64)
    private String phone;

    @Column(length = 64)
    @Comment("密保问题")
    private String question;

    @Column(length = 64)
    @Comment("密保答案")
    private String answer;

    @Column(length = 255)
    @Comment("头像")
    private String avatar;

    @Comment("会员等级")
    @Enumerated(value = EnumType.STRING)
    private PlanLevel plan;

    @Comment("总空间")
    private Long totalQuota;

    @Comment("已使用空间")
    private Long usedQuota;

    @Comment("角色")
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;
}
