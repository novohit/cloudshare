package com.cloudshare.server.file.converter;

import com.cloudshare.server.file.controller.response.FileListRepDTO;
import com.cloudshare.server.file.model.FileDocument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper
public interface FileConverter {

    FileConverter INSTANCE = Mappers.getMapper(FileConverter.class);

    FileListRepDTO DO2VO(FileDocument fileDocument);
}
