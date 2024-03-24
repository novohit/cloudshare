package com.cloudshare.server.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.converter.FileConverter;
import com.cloudshare.server.dto.requset.TrashDeleteReqDTO;
import com.cloudshare.server.dto.requset.TrashListReqDTO;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.enums.FileType;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.repository.FileRepository;
import com.cloudshare.server.service.FileService;
import com.cloudshare.server.service.TrashService;
import com.cloudshare.server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author novo
 * @since 2024/3/17
 */
@Service
public class TrashServiceImpl implements TrashService {

    private final FileRepository fileRepository;

    private final FileConverter fileConverter;

    private final FileService fileService;

    private final UserService userService;

    public TrashServiceImpl(FileRepository fileRepository, FileConverter fileConverter, FileService fileService, UserService userService) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
        this.fileService = fileService;
        this.userService = userService;
    }

    @Override
    public List<FileVO> list(TrashListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectoryAndDeletedAtIsNotNull(userId, reqDTO.curDirectory());
        List<FileVO> resp = fileConverter.DOList2VOList(fileList);
        return resp;
    }

    @Override
    @Transactional
    public void physicallyDelete(List<Long> fileIds) {
        Long userId = UserContextThreadHolder.getUserId();
        List<FileDocument> list = fileRepository.findByFileIdInAndUserId(fileIds, userId);
        List<FileDocument> delete = new ArrayList<>(list);
        for (FileDocument file : list) {
            if (FileType.DIR.equals(file.getType())) {
                // 查询文件夹下的所有文件
                List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNotNull(file.getPath(), userId);
                delete.addAll(subList);
            }
        }
        fileRepository.deleteAllInBatch(delete);
        // TODO 定时任务删除物理文件
    }

    @Override
    @Transactional
    public void recover(List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        long size = fileService.calculateSize(fileIds);
        fileService.checkQuota(size);

        Long userId = UserContextThreadHolder.getUserId();

        List<FileDocument> list = fileRepository.findByFileIdInAndUserId(fileIds, userId);
        List<FileDocument> updateList = new ArrayList<>(list);
        for (FileDocument file : list) {
            file.setDeletedAt(null);
            String originalPath = file.getPath();
            if (FileType.DIR.equals(file.getType())) {
                // 查询文件夹下的所有文件
                List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNotNull(originalPath, userId);
                for (FileDocument subFile : subList) {
                    subFile.setDeletedAt(null);
                }
                updateList.addAll(subList);
            }
        }
        fileRepository.saveAll(updateList);
        userService.incrementQuota(size, userId);
    }
}
