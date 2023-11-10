package org.novo.datasync;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author novo
 * @since 2023/11/10
 */
@SpringBootTest
public class ElasticSearchTest {

    private RestHighLevelClient restHighLevelClient;

    @BeforeEach
    void setUp() {
        String servers = System.getenv("ELASTIC_SEARCH_SERVERS");
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create(servers)
        ));
    }

    @AfterEach
    void shutdown() throws IOException {
        restHighLevelClient.close();
    }

    @Test
    void creatIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hotel");

        request.source("", XContentType.JSON);

        restHighLevelClient.indices()
                .create(request, RequestOptions.DEFAULT);
    }
}
