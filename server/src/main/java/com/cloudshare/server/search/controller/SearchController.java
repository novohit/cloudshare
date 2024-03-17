package com.cloudshare.server.search.controller;

import com.cloudshare.server.dto.response.FileVO;
import com.cloudshare.server.search.controller.request.SearchReqDTO;
import com.cloudshare.server.search.controller.response.SearchHistoryResp;
import com.cloudshare.server.search.service.SearchService;
import com.cloudshare.web.response.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author novo
 * @since 2023/11/9
 */
@RestController
@RequestMapping
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/file/search")
    public Response<List<FileVO>> search(@Validated @RequestBody SearchReqDTO reqDTO) {
        return Response.success(searchService.search(reqDTO));
    }

    @GetMapping("/search/history")
    public Response<List<SearchHistoryResp>> history(@RequestParam(required = false, defaultValue = "5") Integer size) {
        return Response.success(searchService.history(size));
    }
}
