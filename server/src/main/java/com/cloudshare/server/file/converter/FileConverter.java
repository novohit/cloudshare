package com.cloudshare.server.file.converter;

import com.cloudshare.server.file.controller.response.FileListVO;
import com.cloudshare.server.file.model.FileDocument;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    FileListVO DO2VO(FileDocument fileDocument);

    List<FileListVO> DOList2VOList(List<FileDocument> fileList);
}
