package com.cloudshare.server.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.converter.FileConverter;
import com.cloudshare.server.dto.requset.TrashListReqDTO;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.repository.FileRepository;
import com.cloudshare.server.service.TrashService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author novo
 * @since 2024/3/17
 */
@Service
public class TrashServiceImpl implements TrashService {

    private final FileRepository fileRepository;

    private final FileConverter fileConverter;

    public TrashServiceImpl(FileRepository fileRepository, FileConverter fileConverter) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
    }

    @Override
    public List<FileVO> list(TrashListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectoryAndDeletedAtIsNotNull(userId, reqDTO.curDirectory());
        List<FileVO> resp = fileConverter.DOList2VOList(fileList);
        return resp;
    }
}
