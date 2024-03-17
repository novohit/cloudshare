package com.cloudshare.server.service;

import com.cloudshare.server.dto.requset.TrashListReqDTO;
import com.cloudshare.server.dto.response.FileVO;

import java.util.List;

/**
 * @author novo
 * @since 2024/3/17
 */
public interface TrashService {

    List<FileVO> list(TrashListReqDTO reqDTO);
}
