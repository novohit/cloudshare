package com.cloudshare.server.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
 * @since 2023/10/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "user_auth")
public class UserAuth extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("第三方登录类型")
    private LoginType loginType;

    @Column(length = 64, nullable = false)
    @Comment("第三方登录唯一标识")
    private String identify;
}
