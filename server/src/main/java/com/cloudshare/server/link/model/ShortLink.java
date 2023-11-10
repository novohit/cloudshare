package com.cloudshare.server.link.model;

import com.cloudshare.server.common.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author novo
 * @since 2023/11/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "short_link")
public class ShortLink extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 3589181063653354558L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long shareId;


    private String originalUrl;

    /**
     * 短链域名
     */
    private String shortUrl;

    /**
     * 短链码
     */
    private String code;

    private Long pv;

    private Long uv;
}
