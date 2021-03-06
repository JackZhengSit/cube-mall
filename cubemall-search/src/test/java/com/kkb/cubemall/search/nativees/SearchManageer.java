package com.kkb.cubemall.search.nativees;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
public class SearchManageer {
    RestHighLevelClient client;

    @BeforeEach
    public void init(){
        final CredentialsProvider credentialsProvider=new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("elastic","jack@elastic"));

        client=new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("139.224.55.63",9200, "http")).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }));
    }


    @Test
    public void mathAll() throws IOException {
        SearchRequest request =new SearchRequest("blog_2")
                .source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        SearchResponse response=client.search(request, RequestOptions.DEFAULT);
        printResponse(response);
    }

    @Test
    public void termQuery() throws IOException {
        SearchRequest request=new SearchRequest("blog_2")
                .source(new SearchSourceBuilder().query(QueryBuilders.termQuery("title","??????")));
        SearchResponse response=client.search(request, RequestOptions.DEFAULT);
        printResponse(response);
    }

    @Test
    public void highlight() throws IOException {
        SearchRequest request=new SearchRequest("blog_2")
                .source(new SearchSourceBuilder()

                        .query(QueryBuilders.matchQuery("title","???????????????"))

                        .postFilter(QueryBuilders.termQuery("title","??????"))

                        .highlighter(new HighlightBuilder()
                                .field("title")
                                .field("content")
                                .preTags("<em>")
                                .postTags("</em>")
                        )
                        .from(0)
                        .size(30)
                );
        SearchResponse response=client.search(request, RequestOptions.DEFAULT);
        highlightResultPrint(response);
    }


    private void printResponse(SearchResponse response){
        long total=response.getHits().getTotalHits().value;
        log.info("???????????????{}",total);
        Stream.of(response.getHits().getHits()).forEach(hit-> log.info(hit.getSourceAsString()));
    }

    private void highlightResultPrint(SearchResponse response){
        long total=response.getHits().getTotalHits().value;
        log.info("???????????????{}",total);
        Stream.of(response.getHits().getHits()).forEach(hit-> log.info(String.valueOf(hit.getHighlightFields())));
    }


    @AfterEach
    public void release() throws IOException {
        client.close();
    }
}
