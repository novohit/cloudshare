package com.cloudshare.server.file.converter;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.server.file.controller.response.DirTreeNode;
import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.file.model.FileDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(source = "type", target = "fileType")
    @Mapping(source = "name", target = "fileName")
    @Mapping(target = "size", expression = "java(readableFileSize(fileDocument.getSize()))")
    FileVO DO2VO(FileDocument fileDocument);


    List<FileVO> DOList2VOList(List<FileDocument> fileList);

    @Mapping(target = "children", ignore = true)
    DirTreeNode DO2DirTreeNode(FileDocument fileDocument);

    List<DirTreeNode> DOList2TreeNodeList(List<FileDocument> fileList);

    default String readableFileSize(Long size) {
        return FileUtil.readableFileSize(size);
    }
}
