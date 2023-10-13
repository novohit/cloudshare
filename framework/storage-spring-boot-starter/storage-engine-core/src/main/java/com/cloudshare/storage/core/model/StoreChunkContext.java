package com.cloudshare.storage.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author novo
 * @since 2023/10/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StoreChunkContext extends StoreContext {

    private Long chunk;

    private String md5;
}
