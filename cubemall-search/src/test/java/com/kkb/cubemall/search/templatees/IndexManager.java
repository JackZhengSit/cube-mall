package com.kkb.cubemall.search.templatees;


import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class IndexManager {

    @Autowired
    private RestHighLevelClient client;


    @Test
    void testAggs() throws IOException {
        SearchRequest request = new SearchRequest()
                .indices("blog_1")
                .source(new SearchSourceBuilder()
                        .query(QueryBuilders.matchAllQuery())
                        .aggregation(new ValueCountAggregationBuilder("doc_count").field("mobile"))
                        .aggregation(new TermsAggregationBuilder("group_count").field("mobile").size(10))

                );
    }

}
