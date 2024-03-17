package com.cloudshare.server.search.service.impl;

import com.cloudshare.server.auth.UserContextThreadHolder;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.converter.FileConverter;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.repository.FileRepository;
import com.cloudshare.server.search.controller.request.SearchReqDTO;
import com.cloudshare.server.search.controller.response.SearchHistoryResp;
import com.cloudshare.server.search.service.SearchService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author novo
 * @since 2023/11/9
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final FileRepository fileRepository;

    private final FileConverter fileConverter;

    private final StringRedisTemplate stringRedisTemplate;

    private final Integer HISTORY_MAX_SIZE = 10;


    public SearchServiceImpl(FileRepository fileRepository, FileConverter fileConverter, StringRedisTemplate stringRedisTemplate) {
        this.fileRepository = fileRepository;
        this.fileConverter = fileConverter;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public List<FileVO> search(SearchReqDTO reqDTO) {
        Long userId = UserContextThreadHolder.getUserId();
        String curDirectory = reqDTO.curDirectory();
        String keyword = reqDTO.keyword();
        Set<FileDocument> searchResp = new HashSet<>();

        saveSearchHistory(userId, keyword);
        List<FileDocument> namesLike = fileRepository.findByNameContainingAndCurDirectoryAndUserIdAndDeletedAtIsNull(keyword, curDirectory, userId);
        List<FileDocument> contentLike = fileRepository.findByContentContainingAndCurDirectoryAndUserIdAndDeletedAtIsNull(keyword, curDirectory, userId);
        searchResp.addAll(namesLike);
        searchResp.addAll(contentLike);

        List<FileVO> resp = fileConverter.DOList2VOList(searchResp.stream().toList());
        return resp;
    }

    @Override
    public List<SearchHistoryResp> history(Integer size) {
        Long userId = UserContextThreadHolder.getUserId();
        String key = BizConstant.SEARCH_HISTORY_PREFIX + userId;

        List<SearchHistoryResp> history = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, size - 1);
        if (!CollectionUtils.isEmpty(tuples)) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                history.add(new SearchHistoryResp(tuple.getValue()));
            }
        }
        return history;
    }


    private void saveSearchHistory(Long userId, String keyword) {
        String key = BizConstant.SEARCH_HISTORY_PREFIX + userId;
        stringRedisTemplate.opsForZSet().add(key, keyword, System.currentTimeMillis());
        Long size = stringRedisTemplate.opsForZSet().zCard(key);
        if (size > HISTORY_MAX_SIZE) {
            stringRedisTemplate.opsForZSet().removeRange(key, 0, size - HISTORY_MAX_SIZE - 1);
        }
    }
}
