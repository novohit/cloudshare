package com.cloudshare.server.converter;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.server.dto.response.DirTreeNode;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.enums.FileType;
import com.cloudshare.server.model.FileDocument;
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
    @Mapping(target = "readableSize", expression = "java(readableFileSize(fileDocument))")
    FileVO DO2VO(FileDocument fileDocument);


    List<FileVO> DOList2VOList(List<FileDocument> fileList);

    @Mapping(target = "children", ignore = true)
    DirTreeNode DO2DirTreeNode(FileDocument fileDocument);

    List<DirTreeNode> DOList2TreeNodeList(List<FileDocument> fileList);

    default String readableFileSize(FileDocument file) {
        if (FileType.DIR.equals(file.getType())) {
            return "-";
        }
        return FileUtil.readableFileSize(file.getSize());
    }
}
