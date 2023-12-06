package org.novo.datasync.controller;

import org.novo.datasync.model.FileDoc;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author novo
 * @since 2023/12/3
 */
public class SearchController {

    private final ElasticsearchRestTemplate restTemplate;

    public SearchController(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/file/search")
    public void queryHighlight(@RequestParam("keyword") String keyword) {
        Criteria criteria = new Criteria();
        criteria.and(new Criteria("name").matches(keyword));
        criteria.and(new Criteria("content").matches(keyword));
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        HighlightFieldParameters parameters = new HighlightFieldParameters.HighlightFieldParametersBuilder()
                .withPreTags(new String[]{"<span style='color:red'>"})
                .withPostTags(new String[]{"</span>"})
                .build();
        criteriaQuery.setHighlightQuery(new HighlightQuery(new Highlight(Arrays.asList(new HighlightField("name", parameters), new HighlightField("content", parameters))), FileDoc.class));
        SearchHits<FileDoc> searchHits = restTemplate.search(criteriaQuery, FileDoc.class);

        List<FileDoc> resp = new ArrayList<>();
        for (SearchHit<FileDoc> searchHit : searchHits) {
            FileDoc content = searchHit.getContent();
            //将高亮的字段取出来
            List<String> requestBody = searchHit.getHighlightField("name");
            StringBuilder highText = new StringBuilder();
            for (String s : requestBody) {
                highText.append(s);
            }
            //重新对字段赋值
            content.setName(highText.toString());
            resp.add(content);
        }
    }
}
