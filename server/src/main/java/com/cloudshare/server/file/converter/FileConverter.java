package com.cloudshare.server.file.converter;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.server.file.controller.response.FileListVO;
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
    FileListVO DO2VO(FileDocument fileDocument);

    @Mapping(source = "type", target = "fileType")
    @Mapping(source = "name", target = "fileName")
    @Mapping(target = "size", expression = "java(readableFileSize(size))")
    List<FileListVO> DOList2VOList(List<FileDocument> fileList);

    default String readableFileSize(Long size) {
        return FileUtil.readableFileSize(size);
    }
}
