package com.cloudshare.server.common;

import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/10/5
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // https://blog.51cto.com/u_7341513/6089786
@Data
public abstract class BaseModel {

    @CreatedDate
    @Comment("创建时间")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Comment("更改时间")
    private LocalDateTime updatedAt;

    @Comment("删除时间")
    private LocalDateTime deletedAt;
}
