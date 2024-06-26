package com.cloudshare.server.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.link.model.ShortLink;
import com.cloudshare.server.enums.ShareStatus;
import com.cloudshare.server.enums.VisibleType;
import com.cloudshare.server.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

    @Column(unique = true, nullable = false)
    private Long shareId;

    @Comment("分享的文件id")
    @Column(nullable = false)
    private Long fileId;

    @Comment("分享者")
    @Column(nullable = false)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "fileId", referencedColumnName = "fileId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private FileDocument fileDocument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "userId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "shareId", referencedColumnName = "shareId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ShortLink shortLink;
}
