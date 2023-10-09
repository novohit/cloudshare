package com.cloudshare.server.file.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.file.controller.requset.DirAddReqDTO;
import com.cloudshare.server.file.controller.requset.DirRenameReqDTO;
import com.cloudshare.server.file.controller.requset.DirUpdateReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.response.FileListVO;
import com.cloudshare.server.file.converter.FileConverter;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.web.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author novo
 * @since 2023/10/8
 */
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    private final FileConverter fileConverter;

    public FileServiceImpl(FileRepository fileRepository, FileConverter fileConverter) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
    }

    @Override
    public void addDir(DirAddReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        String name = reqDTO.name();
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectoryAndNameStartsWith(userId, reqDTO.curDirectory(), name);
        // 第一个括号 (\\d+) 是一个分组 用于匹配一个或多个数字
        // 第二个括号是字面量
        // Pattern.quote(name) 方法来确保将 name 视为普通的字符串，而不是正则表达式的一部分。
        String regex = "^" + Pattern.quote(name) + "\\((\\d+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        String newName = name;
        if (!CollectionUtils.isEmpty(fileList)) {
            // exist AA add A
            AtomicBoolean hasSame = new AtomicBoolean(false);
            int max = fileList.stream()
                    .map(FileDocument::getName)
                    .mapToInt(fileName -> {
                                Matcher matcher = pattern.matcher(fileName);
                                if (fileName.equals(name)) {
                                    hasSame.set(true);
                                }
                                return matcher.matches() ? Integer.parseInt(matcher.group(1)) : 0;
                            }
                    ).max()
                    .orElse(-1);
            if (hasSame.get()) {
                newName = name + "(%d)".formatted(max + 1);
            }
        }

        FileDocument fileDocument = new FileDocument();
        BeanUtils.copyProperties(reqDTO, fileDocument);
        fileDocument.setName(newName);
        fileDocument.setUserId(userId);
        fileDocument.setType(FileType.DIR);
        fileDocument.setPath(reqDTO.curDirectory() + newName);
        fileDocument.setSize(0L);
        fileRepository.save(fileDocument);
    }

    @Override
    @Transactional
    public void updateDir(DirUpdateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        // 非移动目录
        if (!StringUtils.hasText(reqDTO.newDirectory())) {
            fileRepository.findByUserIdAndCurDirectoryAndName(userId, reqDTO.curDirectory(), reqDTO.name())
                    .ifPresentOrElse((fileDocument) -> {
                        if (!fileDocument.getId().equals(reqDTO.id())) {
                            throw new BadRequestException("已存在该目录");
                        }
                    }, () -> {
                        FileDocument fileDocument = new FileDocument();
                        BeanUtils.copyProperties(reqDTO, fileDocument);
                        int rows = fileRepository.updateDirByIdAndUserId(reqDTO.id(), userId, reqDTO.name(), reqDTO.description());
                        if (rows <= 0) {
                            throw new BadRequestException("更新失败");
                        }
                    });
        } else {

        }

    }

    @Override
    @Transactional
    public void renameDir(DirRenameReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        fileRepository.findByUserIdAndCurDirectoryAndName(userId, reqDTO.curDirectory(), reqDTO.newName())
                .ifPresentOrElse((fileDocument) -> {
                    if (!fileDocument.getId().equals(reqDTO.id())) {
                        throw new BadRequestException("已存在该目录");
                    }
                    throw new BadRequestException("更新失败");
                }, () -> {
                    String newPath = reqDTO.curDirectory() + reqDTO.newName();
                    int rows = fileRepository.renameDir(reqDTO.id(), userId, reqDTO.oldName(), reqDTO.newName(), newPath);
                    if (rows <= 0) {
                        throw new BadRequestException("更新失败");
                    }
                });
    }

    @Override
    public List<FileListVO> list(FileListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        // 一级文件列表
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectory(userId, reqDTO.curDirectory());
        List<FileListVO> voList = fileConverter.DOList2VOList(fileList);
        return voList;
    }
}
