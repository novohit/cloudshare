package com.cloudshare.server.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.common.util.SnowflakeUtil;
import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.file.controller.requset.DirCreateReqDTO;
import com.cloudshare.server.file.controller.requset.DirUpdateReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkMergeReqDTO;
import com.cloudshare.server.file.controller.requset.FileChunkUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileDeleteReqDTO;
import com.cloudshare.server.file.controller.requset.FileListReqDTO;
import com.cloudshare.server.file.controller.requset.FileMoveOrCopyReqDTO;
import com.cloudshare.server.file.controller.requset.FileRenameReqDTO;
import com.cloudshare.server.file.controller.requset.FileSecUploadReqDTO;
import com.cloudshare.server.file.controller.requset.FileSingleUploadReqDTO;
import com.cloudshare.server.file.controller.response.DirTreeNode;
import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.file.converter.FileConverter;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileChunk;
import com.cloudshare.server.file.model.FileDocument;
import com.cloudshare.server.file.repository.FileChunkRepository;
import com.cloudshare.server.file.repository.FileRepository;
import com.cloudshare.server.file.service.FileService;
import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.ReadContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.web.exception.BadRequestException;
import com.cloudshare.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author novo
 * @since 2023/10/8
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    private final FileConverter fileConverter;

    private final StorageEngine storageEngine;

    private final FileChunkRepository fileChunkRepository;

    private final TransactionTemplate transactionTemplate;

    public FileServiceImpl(FileRepository fileRepository, FileConverter fileConverter,
                           StorageEngine storageEngine, FileChunkRepository fileChunkRepository,
                           TransactionTemplate transactionTemplate) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
        this.storageEngine = storageEngine;
        this.fileChunkRepository = fileChunkRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void createDir(DirCreateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        String name = reqDTO.dirName();
        FileDocument dir = assembleFileDocument(
                userId,
                reqDTO.parentId(),
                null,
                name,
                null,
                reqDTO.curDirectory(),
                reqDTO.curDirectory() + name + BizConstant.LINUX_SEPARATOR,
                null,
                0L,
                FileType.DIR,
                null
        );
        saveFile2DB(dir, true);
    }

    @Override
    @Transactional
    public void updateDir(DirUpdateReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        // 非移动目录
        if (!StringUtils.hasText(reqDTO.newDirectory())) {
            fileRepository.findByUserIdAndCurDirectoryAndNameAndDeletedAtIsNull(userId, reqDTO.curDirectory(), reqDTO.name())
                    .ifPresentOrElse((fileDocument) -> {
                        if (!fileDocument.getId().equals(reqDTO.id())) {
                            throw new BadRequestException("已存在该目录");
                        }
                    }, () -> {
                        FileDocument fileDocument = new FileDocument();
                        BeanUtils.copyProperties(reqDTO, fileDocument);
                        int rows = fileRepository.updateDirByFileIdAndUserId(reqDTO.id(), userId, reqDTO.name(), reqDTO.description());
                        if (rows <= 0) {
                            throw new BadRequestException("更新失败");
                        }
                    });
        } else {

        }

    }

    @Override
    public void rename(FileRenameReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        String newName = reqDTO.newName();
        if (reqDTO.oldName().equals(newName)) {
            return;
        }
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(reqDTO.fileId(), userId);
        if (optional.isPresent()) {
            FileDocument file = optional.get();
            String originalPath = file.getPath();
            String originalCurDirectory = file.getCurDirectory();
            file.setName(newName);
            if (!FileType.DIR.equals(file.getType())) {
                file.setPath(originalCurDirectory + newName);
                saveFile2DB(file, false);
            } else {
                try {
                    transactionTemplate.execute(status -> {
                        file.setPath(originalCurDirectory + newName + BizConstant.LINUX_SEPARATOR);
                        saveFile2DB(file, false);
                        List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNull(originalPath, userId);
                        for (FileDocument sub : subList) {
                            // rename 是换旧目录后面的
                            // move 是换旧目录前面的
                            sub.setCurDirectory(sub.getCurDirectory().replaceFirst(originalPath, file.getPath()));
                            sub.setPath(sub.getPath().replaceFirst(originalPath, file.getPath()));
                            saveFile2DB(sub, false);
                        }
                        return null;
                    });
                } catch (DataIntegrityViolationException e) {
                    throw new BizException(BizConstant.REPEAT_NAME);
                }
            }
        }
    }

    /**
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
            context.setFileName(multipartFile.getOriginalFilename());
            storageEngine.store(context);
            String suffix = FileUtil.getSuffix(context.getFileName());
            // hutool return suffix have no dot
            suffix = suffix.isEmpty() ? "" : BizConstant.DOT + suffix;
            FileDocument fileDocument = assembleFileDocument(
                    userId,
                    reqDTO.parentId(),
                    reqDTO.md5(),
                    context.getFileName(),
                    null,
                    reqDTO.curDirectory(),
                    reqDTO.curDirectory() + context.getFileName(),
                    context.getRealPath(),
                    context.getTotalSize(),
                    FileType.suffix2Type(suffix),
                    suffix
            );
            saveFile2DB(fileDocument, true);
        } catch (IOException e) {
            log.error("文件上传异常", e);
            throw new BizException("文件上传异常");
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
        List<FileDocument> fileList = fileRepository.findByMd5AndDeletedAtIsNull(reqDTO.md5());
        if (CollectionUtils.isEmpty(fileList)) {
            return false;
        }
        FileDocument same = fileList.get(0);
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
                reqDTO.curDirectory() + reqDTO.fileName(),
                same.getRealPath(),
                same.getSize(),
                FileType.suffix2Type(suffix),
                suffix
        );
        saveFile2DB(fileDocument, true);
        return true;
    }

    /**
     * TODO 并发读取分片数量优化
     *
     * @param reqDTO
     * @return
     */
    @Override
    @Transactional
    public synchronized boolean chunkUpload(FileChunkUploadReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        try {
            // 上传分片
            MultipartFile multipartFile = reqDTO.file();
            StoreChunkContext context = new StoreChunkContext();
            context.setChunkNum(reqDTO.chunkNum());
            context.setMd5(reqDTO.md5());
            context.setTotalSize(multipartFile.getSize());
            context.setInputStream(multipartFile.getInputStream());
            context.setFileName(reqDTO.fileName());
            storageEngine.storeChunk(context);
            // 保存分片记录
            // 前端分片上传组件会出现取消上传但请求已经发出的情况

            Optional<FileChunk> optional = fileChunkRepository.findByChunkInfoAndUserIdAndDeletedAtIsNull(context.getChunkInfo(), userId);
            if (optional.isEmpty()) {
                FileChunk fileChunk = new FileChunk();
                fileChunk.setName(reqDTO.fileName());
                fileChunk.setUserId(userId);
                fileChunk.setChunkNum(reqDTO.chunkNum());
                fileChunk.setChunkSize(multipartFile.getSize());
                fileChunk.setMd5(reqDTO.md5());
                fileChunk.setChunkInfo(context.getChunkInfo());
                fileChunkRepository.save(fileChunk);
            }
            // 判断所有分片是否上传完毕
            List<FileChunk> chunks = fileChunkRepository.findByMd5AndUserIdAndDeletedAtIsNull(reqDTO.md5(), userId);
            return chunks.size() == reqDTO.totalChunkSize();
        } catch (IOException e) {
            log.error("文件上传异常", e);
            throw new BizException("文件上传异常");
        }
    }

    @Override
    public List<Integer> chunkUpload(String md5) {
        // TODO 是否允许不同用户公用分片
        Long userId = UserContextThreadHolder.getUserId();
        List<FileChunk> chunks = fileChunkRepository.findByMd5AndUserIdAndDeletedAtIsNull(md5, userId);
        return chunks.stream().map(FileChunk::getChunkNum).toList();
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
        List<String> chunkRealPathList = chunks
                .stream()
                .sorted(Comparator.comparing(FileChunk::getChunkNum)) // 先根据分片排序
                .map(chunk -> {
                    totalSize.addAndGet(chunk.getChunkSize());
                    return chunk.getChunkInfo();
                })
                .toList();

        try {
            // 1. 分片文件合并
            MergeChunkContext context = new MergeChunkContext();
            context.setFileName(reqDTO.fileName());
            context.setChunkInfoList(chunkRealPathList);
            storageEngine.mergeChunk(context);

            // 2. 删除分片记录和分片文件 交给定时任务 TODO
            List<FileChunk> deletedChunks = chunks
                    .stream()
                    .peek(chunk -> chunk.setDeletedAt(LocalDateTime.now()))
                    .toList();
            fileChunkRepository.saveAll(deletedChunks);
            // 3. 保存合并记录
            String suffix = FileUtil.getSuffix(context.getFileName());
            // hutool return suffix have no dot
            suffix = suffix.isEmpty() ? "" : BizConstant.DOT + suffix;
            FileDocument fileDocument = assembleFileDocument(
                    userId,
                    reqDTO.parentId(),
                    reqDTO.md5(),
                    reqDTO.fileName(),
                    null,
                    reqDTO.curDirectory(),
                    reqDTO.curDirectory() + reqDTO.fileName(),
                    context.getRealPath(),
                    totalSize.get(),
                    FileType.suffix2Type(suffix),
                    suffix
            );
            saveFile2DB(fileDocument, true);
        } catch (IOException e) {
            log.error("文件合并异常", e);
            throw new BizException("文件合并异常");
        }
    }

    @Override
    public void download(Long fileId, Long userId, HttpServletResponse response) {
        // 1. 校验下载权限
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
        if (optional.isEmpty()) {
            throw new BizException("文件不存在");
        }
        FileDocument fileDocument = optional.get();
        if (FileType.DIR.equals(fileDocument.getType())) {
            // TODO support dir download
            throw new BizException("暂不支持文件夹下载");
        }
        // 2. 返回文件流
        try {
            // 兼容所有 client https://xie.infoq.cn/article/7907c0965b06ae114fa1079c5
            String fileName = URLEncoder.encode(fileDocument.getName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName +
                            ";filename*=utf-8''" + fileName);

            ReadContext context = new ReadContext();
            context.setRealPath(fileDocument.getRealPath());
            context.setOutputStream(response.getOutputStream());
            storageEngine.read(context);
        } catch (IOException e) {
            log.error("文件读取异常", e);
            throw new BizException("文件不存在");
        }
    }

    @Override
    public void preview(Long fileId, HttpServletResponse response) {
        Long userId = UserContextThreadHolder.getUserId();
        // 1. 校验下载权限
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
        if (optional.isEmpty()) {
            throw new BizException("文件不存在");
        }
        FileDocument fileDocument = optional.get();
        if (FileType.DIR.equals(fileDocument.getType())) {
            // TODO support dir download
            throw new BizException("暂不支持文件夹预览");
        }
        // 2. 返回文件流
        try {
            // TODO set content-type
            ReadContext context = new ReadContext();
            context.setRealPath(fileDocument.getRealPath());
            context.setOutputStream(response.getOutputStream());
            storageEngine.read(context);
        } catch (IOException e) {
            log.error("文件读取异常", e);
            throw new BizException("文件不存在");
        }
    }

    @Override
    public void delete(FileDeleteReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        if (CollectionUtils.isEmpty(reqDTO.fileIds())) {
            return;
        }
        List<FileDocument> list = fileRepository.findByFileIdInAndUserId(reqDTO.fileIds(), userId);
        List<FileDocument> delete = new ArrayList<>(list);
        for (FileDocument file : list) {
            // 文件直接设置删除时间
            file.setDeletedAt(LocalDateTime.now());
            if (FileType.DIR.equals(file.getType())) {
                // 查询文件夹下的所有文件
                List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNull(file.getPath(), userId);
                for (FileDocument subFile : subList) {
                    subFile.setDeletedAt(LocalDateTime.now());
                }
                delete.addAll(subList);
            }
            // TODO 定时任务执行物理删除
        }
        fileRepository.saveAll(delete);
    }

    /**
     * 唯一一个用了 parentId 的方法
     * TODO 根 parentId 魔法值
     *
     * @return
     */
    @Override
    public List<DirTreeNode> dirTree() {
        // TODO set cache
        Long userId = UserContextThreadHolder.getUserId();
        List<FileDocument> list = fileRepository.findByUserIdAndTypeAndDeletedAtIsNull(userId, FileType.DIR);
        List<DirTreeNode> nodes = fileConverter.DOList2TreeNodeList(list);

        Map<Long, DirTreeNode> map = nodes.stream()
                .collect(Collectors.toMap(DirTreeNode::getFileId, Function.identity()));

        // 构建目录树
        List<DirTreeNode> tree = new ArrayList<>();
        for (DirTreeNode node : nodes) {
            Long parentId = node.getParentId();
            // 根目录
            if (parentId == BizConstant.ROOT_PARENT_ID || !map.containsKey(parentId)) {
                tree.add(node);
            } else {
                DirTreeNode parentNode = map.get(parentId);
                parentNode.getChildren().add(node);
            }
        }
        DirTreeNode root = new DirTreeNode(BizConstant.ROOT_PARENT_ID, null, "/", "/", "/", tree);
        return Collections.singletonList(root);
    }

    @Override
    public FileVO detail(Long fileId, Long userId) {
        Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
        if (optional.isEmpty()) {
            throw new BizException("文件不存在");
        }
        return fileConverter.DO2VO(optional.get());
    }

    @Override
    public boolean isSubFile(Long rootFileId, Long subFileId, Long userId) {
        Optional<FileDocument> subOptional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(subFileId, userId);
        Optional<FileDocument> rootOptional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(rootFileId, userId);
        if (subOptional.isPresent() && rootOptional.isPresent()) {
            FileDocument root = rootOptional.get();
            FileDocument sub = subOptional.get();
            if (sub.getId().equals(root.getId())) {
                return true;
            }
            if (!FileType.DIR.equals(root.getType())) {
                return false;
            }
            return sub.getCurDirectory().startsWith(root.getPath());
        }
        return false;
    }

    @Override
    public void move(FileMoveOrCopyReqDTO reqDTO) {
        // TODO 性能优化
        Long userId = UserContextThreadHolder.getUserId();
        List<Long> fileIds = reqDTO.fileIds();
        for (Long fileId : fileIds) {
            Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, userId);
            if (optional.isPresent()) {
                FileDocument file = optional.get();
                String originalPath = file.getPath();
                String originalCurDirectory = file.getCurDirectory();
                file.setCurDirectory(reqDTO.target());
                file.setParentId(reqDTO.parentId());
                if (!FileType.DIR.equals(file.getType())) {
                    file.setPath(reqDTO.target() + file.getName());
                    saveFile2DB(file, false);
                } else {
                    try {
                        transactionTemplate.execute(status -> {
                            file.setPath(reqDTO.target() + file.getName() + BizConstant.LINUX_SEPARATOR);
                            saveFile2DB(file, false);
                            List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNull(originalPath, userId);
                            for (FileDocument sub : subList) {
                                sub.setCurDirectory(sub.getCurDirectory().replaceFirst(originalCurDirectory, reqDTO.target()));
                                sub.setPath(sub.getPath().replaceFirst(originalCurDirectory, reqDTO.target()));
                                saveFile2DB(sub, false);
                            }
                            return null;
                        });
                    } catch (DataIntegrityViolationException e) {
                        throw new BizException(BizConstant.REPEAT_NAME);
                    }

                }
            }
        }
    }

    @Override
    public void copy(FileMoveOrCopyReqDTO reqDTO, Long sourceUser, Long targetUser) {
        List<Long> fileIds = reqDTO.fileIds();
        String target = reqDTO.target();
        for (Long fileId : fileIds) {
            Optional<FileDocument> optional = fileRepository.findByFileIdAndUserIdAndDeletedAtIsNull(fileId, sourceUser);
            if (optional.isPresent()) {
                FileDocument file = optional.get();
                // copy
                FileDocument copyFile = assembleFileDocument(
                        targetUser,
                        reqDTO.parentId(), // parentId
                        file.getMd5(),
                        file.getName(),
                        file.getRealName(),
                        target, // curDirectory
                        null,
                        file.getRealPath(),
                        file.getSize(),
                        file.getType(),
                        file.getSuffix()
                );

                String originalPath = file.getPath();
                String originalCurDirectory = file.getCurDirectory();
                if (!FileType.DIR.equals(copyFile.getType())) {
                    copyFile.setPath(target + copyFile.getName());
                    saveFile2DB(copyFile, true);
                } else {
                    transactionTemplate.execute(status -> {
                        copyFile.setPath(target + copyFile.getName() + BizConstant.LINUX_SEPARATOR);
                        String newName = saveFile2DB(copyFile, true);
                        List<FileDocument> subList = fileRepository.findByCurDirectoryStartsWithAndUserIdAndDeletedAtIsNull(originalPath, sourceUser);
                        for (FileDocument sub : subList) {
                            FileDocument copySub = assembleFileDocument(
                                    targetUser,
                                    sub.getParentId(), // 子文件的 parentId 不会变
                                    sub.getMd5(),
                                    sub.getName(),
                                    sub.getRealName(),
                                    null,
                                    null,
                                    sub.getRealPath(),
                                    sub.getSize(),
                                    sub.getType(),
                                    sub.getSuffix()
                            );
                            String curDirectory = sub.getCurDirectory().replaceFirst(originalCurDirectory, target);
                            String path = sub.getPath().replaceFirst(originalCurDirectory, target);
                            if (!file.getName().equals(newName)) {
                                curDirectory = renamePath(target, file, newName, curDirectory);
                                path = renamePath(target, file, newName, path);
                            }
                            copySub.setCurDirectory(curDirectory);
                            copySub.setPath(path);
                            saveFile2DB(copySub, true);
                        }
                        return null;
                    });
                }
            }
        }
    }

    @Override
    public List<FileVO> list(FileListReqDTO reqDTO, Long userId) {
        String curDirectory = reqDTO.curDirectory();
        List<FileType> fileTypes = reqDTO.fileTypeList();
        // 一级文件列表
        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectoryAndDeletedAtIsNull(userId, curDirectory);
        if (!CollectionUtils.isEmpty(fileTypes)) {
            fileList = fileList.stream()
                    .filter(fileDocument -> fileTypes.contains(fileDocument.getType()))
                    .toList();
        }
        List<FileVO> resp = fileConverter.DOList2VOList(fileList);
        return resp;
    }


    private String renamePath(String target, FileDocument file, String newName, String path) {
        int startJ = path.indexOf(target);
        if (startJ >= 0) {
            String rightPortion = path.substring(startJ + target.length());
            String leftPortion = path.substring(0, startJ + target.length());
            path = leftPortion + rightPortion.replaceFirst(file.getName(), newName);
        }
        return path;
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
        fileDocument.setFileId(SnowflakeUtil.nextId());
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

    /**
     * 解决重名冲突
     *
     * @param fileDocument
     * @return
     */
    private String saveFile2DB(FileDocument fileDocument, boolean autoCheck) throws DataIntegrityViolationException {
        if (!autoCheck) {
            fileRepository.save(fileDocument);
            return fileDocument.getName();
        }
        Long userId = UserContextThreadHolder.getUserId();
        String curDirectory = fileDocument.getCurDirectory();
        String name = fileDocument.getName();

        List<FileDocument> fileList = fileRepository.findByUserIdAndCurDirectoryAndNameStartsWithAndDeletedAtIsNull(userId, curDirectory, name);
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
        if (!newName.equals(fileDocument.getName())) {
            fileDocument.setName(newName);
            String newPath = fileDocument.getType() == FileType.DIR
                    ? fileDocument.getCurDirectory() + newName + BizConstant.LINUX_SEPARATOR
                    : fileDocument.getCurDirectory() + newName;
            fileDocument.setPath(newPath);
        }

        fileRepository.save(fileDocument);
        return newName;
    }
}
