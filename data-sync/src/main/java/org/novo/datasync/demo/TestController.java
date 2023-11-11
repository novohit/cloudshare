package org.novo.datasync.demo;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author novo
 * @since 2023/11/10
 */
@RestController
@Slf4j
public class TestController {

    private final ElasticsearchRestTemplate restTemplate;

    private final UserDocRepository userDocRepository;

    public TestController(ElasticsearchRestTemplate restTemplate, UserDocRepository userDocRepository) {
        this.restTemplate = restTemplate;
        this.userDocRepository = userDocRepository;
    }

    @GetMapping("/save")
    public void save(@RequestParam("keyword") String keyword) {
        Random random = new Random();
        int i = random.nextInt();
        UserDoc userDoc = new UserDoc(random.nextLong(), keyword + i, "今天天气真好！" + i);
        userDocRepository.save(userDoc);
    }

    @GetMapping("/get")
    public void get() {
        Iterable<UserDoc> all = userDocRepository.findAll();
        Iterator<UserDoc> iterator = all.iterator();
        while (iterator.hasNext()) {
            log.info("{}", iterator.next());
        }
    }

    @GetMapping("/query")
    public void query(@RequestParam("keyword") String keyword) {
        Criteria criteria = new Criteria();
        criteria.and(new Criteria("username").matches(keyword));
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<UserDoc> searchHits = restTemplate.search(criteriaQuery, UserDoc.class);
        List<UserDoc> res = searchHits.get().map(SearchHit::getContent).toList();
        log.info("{}", res);
    }

    @GetMapping("/query/highlight")
    public void queryHighlight(@RequestParam("keyword") String keyword) {
        Criteria criteria = new Criteria();
        criteria.and(new Criteria("username").matches(keyword));
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        HighlightFieldParameters parameters = new HighlightFieldParameters.HighlightFieldParametersBuilder()
                .withPreTags(new String[]{"<span style='color:red'>"})
                .withPostTags(new String[]{"</span>"})
                .build();
        criteriaQuery.setHighlightQuery(new HighlightQuery(new Highlight(Arrays.asList(new HighlightField("username", parameters))), UserDoc.class));
        SearchHits<UserDoc> searchHits = restTemplate.search(criteriaQuery, UserDoc.class);

        List<UserDoc> resp = new ArrayList<>();
        for (SearchHit<UserDoc> searchHit : searchHits) {
            UserDoc content = searchHit.getContent();
            //将高亮的字段取出来
            List<String> requestBody = searchHit.getHighlightField("username");
            StringBuilder highText = new StringBuilder();
            for (String s : requestBody) {
                highText.append(s);
            }
            //重新对字段赋值
            content.setUsername(highText.toString());
            resp.add(content);
        }

        log.info("{}", resp);
    }
}
