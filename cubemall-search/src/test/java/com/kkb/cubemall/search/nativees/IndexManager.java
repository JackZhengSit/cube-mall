package com.kkb.cubemall.search.nativees;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class IndexManager {

    private RestHighLevelClient client;

    @Before
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
    public void create() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("blog_1");
        request.settings(
                Settings.builder()
                        .put("index.number_of_shards",5)
                        .put("index.number_of_replicas",1));

        XContentBuilder builder= XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("id");
                {
                    builder.field("type","long");
                }
                builder.endObject();
                builder.startObject("title");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_smart");
                    builder.field("store","true");
                }
                builder.endObject();
                builder.startObject("content");
                {
                    builder.field("type","text");
                    builder.field("analyzer","ik_smart");
                    builder.field("store","true");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);

        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void create2() throws IOException {
        CreateIndexRequest request=new CreateIndexRequest("blog_2");
        request.source("{\n" +
                "  \"settings\": {\n" +
                "    \"number_of_shards\": 5,\n" +
                "    \"number_of_replicas\": 1\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"mobile\": {\n" +
                "        \"store\": true,\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"comment\": {\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"store\": true,\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      \"id\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"title\": {\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"store\": true,\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      \"content\": {\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"store\": true,\n" +
                "        \"type\": \"text\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}",XContentType.JSON);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }



    @After
    public void release() throws IOException {
        client.close();
    }

}
