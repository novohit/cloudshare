package com.cloudshare.server.order.model;

import com.cloudshare.server.common.BaseModel;
import com.cloudshare.server.user.enums.PlanLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author novo
 * @since 2023/11/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "product")
public class Product extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("商品标题")
    private String title;

    @Comment("商品详情")
    private String detail;

    @Comment("商品价格")
    private BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    private PlanLevel plan;
}
