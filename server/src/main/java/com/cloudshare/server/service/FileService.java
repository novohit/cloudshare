package com.cloudshare.server.service;

import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.dto.requset.DirCreateReqDTO;
import com.cloudshare.server.dto.requset.DirUpdateReqDTO;
import com.cloudshare.server.dto.requset.FileChunkMergeReqDTO;
import com.cloudshare.server.dto.requset.FileChunkUploadReqDTO;
import com.cloudshare.server.dto.requset.FileDeleteReqDTO;
import com.cloudshare.server.dto.requset.FileListReqDTO;
import com.cloudshare.server.dto.requset.FileMoveOrCopyReqDTO;
import com.cloudshare.server.dto.requset.FileRenameReqDTO;
import com.cloudshare.server.dto.requset.FileSecUploadReqDTO;
import com.cloudshare.server.dto.requset.FileSingleUploadReqDTO;
import com.cloudshare.server.dto.response.DirTreeNode;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.dto.response.StatsKeyValue;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
public interface FileService {
    void createDir(DirCreateReqDTO reqDTO);

    List<FileVO> list(FileListReqDTO reqDTO, Long userId);

    void updateDir(DirUpdateReqDTO reqDTO);

    void rename(FileRenameReqDTO reqDTO);

    void singleUpload(FileSingleUploadReqDTO reqDTO);

    Boolean secUpload(FileSecUploadReqDTO reqDTO);

    boolean chunkUpload(FileChunkUploadReqDTO reqDTO);

    List<Integer> chunkUpload(String md5);

    void chunkMerge(FileChunkMergeReqDTO reqDTO);

    void download(Long fileId, Long userId, HttpServletResponse response);

    void preview(Long fileId, HttpServletResponse response);

    void mediaPreview(Long fileId, String range, HttpServletResponse response);

    void logicallyDelete(FileDeleteReqDTO reqDTO);

    List<DirTreeNode> dirTree();

    FileVO detail(Long fileId, Long userId);

    boolean isSubFile(Long rootFileId, Long subFileId, Long userId);

    void move(FileMoveOrCopyReqDTO reqDTO);

    void copy(FileMoveOrCopyReqDTO reqDTO, Long sourceId, UserContext targetUser);

    long calculateSize(List<Long> fileIds);

    void checkQuota(Long fileSize);

    List<StatsKeyValue> statsCount();

    List<StatsKeyValue> statsSize();
}
