package com.cloudshare.server.file.converter;

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
    List<FileListVO> DOList2VOList(List<FileDocument> fileList);
}
