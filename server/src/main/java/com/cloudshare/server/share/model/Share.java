package com.cloudshare.server.share.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.share.enums.ShareStatus;
import com.cloudshare.server.share.enums.VisibleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "share")
public class Share extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("分享的文件id")
    private Long fileId;

    private Long userId;

    @Comment("是否公开，私有则需要提取码")
    @Enumerated(value = EnumType.STRING)
    private VisibleType visibleType;

    @Comment("是否可用，封禁")
    @Enumerated(value = EnumType.STRING)
    private ShareStatus shareStatus;

    @Comment("分享短链")
    private String url;

    @Comment("提取码")
    private String code;

    @Comment("过期时间，为null永久有效")
    private LocalDateTime expiredAt;
}
