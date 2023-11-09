package com.cloudshare.server.search.service;

import com.cloudshare.server.file.controller.response.FileVO;
import com.cloudshare.server.search.controller.request.SearchReqDTO;
import com.cloudshare.server.search.controller.response.SearchHistoryResp;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/9
 */
public interface SearchService {

    List<FileVO> search(SearchReqDTO reqDTO);

    List<SearchHistoryResp> history(Integer size);
}
