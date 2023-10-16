package com.cloudshare.server.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.constant.BizConstant;
import com.cloudshare.server.file.controller.requset.DirAddReqDTO;
import com.cloudshare.server.file.controller.requset.DirRenameReqDTO;
import com.cloudshare.server.file.controller.requset.DirUpdateReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkMergeReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.requset.FileSecUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileSingleUploadReqDTO;
import com.cloudshare.server.file.controller.response.FileListVO;
import com.cloudshare.server.file.converter.FileConverter;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileChunk;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileChunkRepository;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.web.exception.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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

    private final StorageEngine storageEngine;

    private final FileChunkRepository fileChunkRepository;

    public FileServiceImpl(FileRepository fileRepository, FileConverter fileConverter,
                           StorageEngine storageEngine, FileChunkRepository fileChunkRepository) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
        this.storageEngine = storageEngine;
        this.fileChunkRepository = fileChunkRepository;
    }

    @Override
    public void addDir(DirAddReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        String name = reqDTO.dirName();
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

    /**
     * TODO　多次上传同名字文件处理
     *
     * @param reqDTO
     */
    @Override
    @Transactional
    public void singleUpload(FileSingleUploadReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        // 保存实体
        // 上传文件
        try {
            MultipartFile multipartFile = reqDTO.file();
            StoreContext context = new StoreContext();
            context.setTotalSize(multipartFile.getSize());
            context.setInputStream(multipartFile.getInputStream());
            context.setFileNameWithSuffix(multipartFile.getOriginalFilename());
            storageEngine.store(context);
            String suffix = FileUtil.getSuffix(context.getFileNameWithSuffix());
            // hutool return suffix have no dot
            suffix = suffix.isEmpty() ? "" : BizConstant.DOT + suffix;
            FileDocument fileDocument = assembleFileDocument(
                    userId,
                    reqDTO.parentId(),
                    reqDTO.md5(),
                    context.getFileNameWithSuffix(),
                    null,
                    reqDTO.curDirectory(),
                    reqDTO.curDirectory() + BizConstant.LINUX_SEPARATOR + context.getFileNameWithSuffix(),
                    context.getRealPath(),
                    context.getTotalSize(),
                    FileType.suffix2Type(suffix),
                    suffix
            );
            saveFile2DB(fileDocument);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 即使多个用户也共享同一个 md5
     *
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean secUpload(FileSecUploadReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        Optional<FileDocument> optional = fileRepository.findByMd5(reqDTO.md5());
        if (optional.isEmpty()) {
            return false;
        }
        FileDocument same = optional.get();
        String suffix = FileUtil.getSuffix(reqDTO.fileName());
        suffix = suffix.isEmpty() ? "" : BizConstant.DOT + suffix;
        // fileType suffix 以用户新给的文件名为主
        FileDocument fileDocument = assembleFileDocument(
                userId,
                reqDTO.parentId(),
                reqDTO.md5(),
                reqDTO.fileName(),
                null,
                reqDTO.curDirectory(),
                reqDTO.curDirectory() + BizConstant.LINUX_SEPARATOR + reqDTO.fileName(),
                same.getRealPath(),
                same.getSize(),
                FileType.suffix2Type(suffix),
                suffix
        );
        saveFile2DB(fileDocument);
        return true;
    }

    @Override
    @Transactional
    public void chunkUpload(FileChunkUploadReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        try {
            // 上传分片
            MultipartFile multipartFile = reqDTO.file();
            StoreChunkContext context = new StoreChunkContext();
            context.setChunk(reqDTO.chunk());
            context.setMd5(reqDTO.md5());
            context.setTotalSize(multipartFile.getSize());
            context.setInputStream(multipartFile.getInputStream());
            context.setFileNameWithSuffix(reqDTO.fileName());
            storageEngine.storeChunk(context);
            // 保存分片记录
            FileChunk fileChunk = new FileChunk();
            fileChunk.setName(reqDTO.fileName());
            fileChunk.setUserId(userId);
            fileChunk.setChunk(reqDTO.chunk());
            fileChunk.setChunkSize(multipartFile.getSize());
            fileChunk.setMd5(reqDTO.md5());
            fileChunk.setRealPath(context.getRealPath());
            fileChunkRepository.save(fileChunk);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> chunkUpload(String md5) {
        // TODO 是否允许不同用户公用分片
        Long userId = UserContextThreadHolder.getUserId();
        List<FileChunk> chunks = fileChunkRepository.findByMd5AndUserIdAndDeletedAtIsNull(md5, userId);
        return chunks.stream().map(FileChunk::getChunk).toList();
    }

    @Override
    @Transactional
    public void chunkMerge(FileChunkMergeReqDTO reqDTO) {
        // 1. 查询分片文件路径
        Long userId = UserContextThreadHolder.getUserId();
        List<FileChunk> chunks = fileChunkRepository.findByMd5AndUserIdAndDeletedAtIsNull(reqDTO.md5(), userId);
        if (CollectionUtils.isEmpty(chunks)) {
            return;
        }
        AtomicLong totalSize = new AtomicLong(0L);
        List<String> chunkRealPathList = chunks.stream()
                .map(chunk -> {
                    totalSize.addAndGet(chunk.getChunkSize());
                    return chunk.getRealPath();
                })
                .toList();

        try {
            // 1. 分片文件合并
            MergeChunkContext context = new MergeChunkContext();
            context.setFileNameWithSuffix(reqDTO.fileName());
            context.setChunkRealPathList(chunkRealPathList);
            storageEngine.mergeChunk(context);

            // 2. 删除分片记录和分片文件 交给定时任务 TODO
            List<FileChunk> deletedChunks = chunks
                    .stream()
                    .peek(chunk -> chunk.setDeletedAt(LocalDateTime.now()))
                    .toList();
            fileChunkRepository.saveAll(deletedChunks);
            // 3. 保存合并记录
            String suffix = FileUtil.getSuffix(context.getFileNameWithSuffix());
            // hutool return suffix have no dot
            suffix = suffix.isEmpty() ? "" : BizConstant.DOT + suffix;
            FileDocument fileDocument = assembleFileDocument(
                    userId,
                    reqDTO.parentId(),
                    reqDTO.md5(),
                    reqDTO.fileName(),
                    null,
                    reqDTO.curDirectory(),
                    reqDTO.curDirectory() + BizConstant.LINUX_SEPARATOR + reqDTO.fileName(),
                    context.getRealPath(),
                    totalSize.get(),
                    FileType.suffix2Type(suffix),
                    suffix
            );
            saveFile2DB(fileDocument);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<FileListVO> list(FileListReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        // 一级文件列表
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectory(userId, reqDTO.curDirectory());
        List<FileListVO> voList = fileConverter.DOList2VOList(fileList);
        return voList;
    }


    private FileDocument assembleFileDocument(
            Long userId,
            Long parentId,
            String md5,
            String name,
            String realName,
            String curDirectory,
            String path,
            String realPath,
            Long size,
            FileType fileType,
            String suffix
    ) {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setUserId(userId);
        fileDocument.setParentId(parentId);
        fileDocument.setMd5(md5);
        fileDocument.setName(name);
        fileDocument.setCurDirectory(curDirectory);
        fileDocument.setPath(path);
        fileDocument.setRealPath(realPath);
        fileDocument.setSize(size);
        fileDocument.setType(fileType);
        fileDocument.setSuffix(suffix);
        return fileDocument;
    }

    private void saveFile2DB(FileDocument fileDocument) {
        // 正常规则是 /A/B + / + name
        // 当前目录是根目录时会多加一个 /
        if (BizConstant.LINUX_SEPARATOR.equals(fileDocument.getCurDirectory())) {
            fileDocument.setPath(fileDocument.getCurDirectory() + fileDocument.getName());
        }
        fileRepository.save(fileDocument);
    }
}
