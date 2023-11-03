package com.cloudshare.storage.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/10
 */
@Data
public class DeleteContext {

    private List<String> realPathList;
}
