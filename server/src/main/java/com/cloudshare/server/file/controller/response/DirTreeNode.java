package com.cloudshare.server.file.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirTreeNode {
    private Long fileId;

    @JsonIgnore
    private Long parentId;

    private String path;

    @JsonIgnore
    private String curDirectory;

    private String name;

    private List<DirTreeNode> children = new ArrayList<>();
}
