package com.cloudshare.server.file.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.file.api.requset.DirAddReqDTO;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.file.service.FileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author novo
 * @since 2023/10/8
 */
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void addDir(DirAddReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        FileDocument fileDocument = new FileDocument();
        BeanUtils.copyProperties(reqDTO, fileDocument);
        fileDocument.setUserId(userId);
        fileDocument.setType(FileType.DIR);
        fileDocument.setPath(reqDTO.curDirectory() + reqDTO.name());
        fileDocument.setSize(0L);
        fileRepository.save(fileDocument);
    }
}
