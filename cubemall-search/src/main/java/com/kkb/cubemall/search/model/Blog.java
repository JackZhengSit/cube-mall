package com.kkb.cubemall.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "blog_repository",shards = 5,replicas = 1)
public class Blog {

    @Id
    @Field(type = FieldType.Long, store = true)
    private long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String content;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", store = true)
    private String comment;

    @Field(type = FieldType.Keyword, store = true)
    private String mobile;



}
