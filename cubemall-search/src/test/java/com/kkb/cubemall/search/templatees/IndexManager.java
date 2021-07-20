package com.kkb.cubemall.search.templatees;


import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@SpringBootTest
@Slf4j
public class IndexManager {

    @Autowired
    private RestHighLevelClient client;


    @Test
    public void testAggs() throws IOException {
        SearchRequest request = new SearchRequest()
                .indices("blog_2")
                .source(new SearchSourceBuilder()
                        .query(QueryBuilders.matchAllQuery())
                        .aggregation(AggregationBuilders.terms("group_count").field("mobile").size(10))
                        .aggregation(AggregationBuilders.count("count").field("mobile"))

                );
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        log.info("{}",response);
    }

}
