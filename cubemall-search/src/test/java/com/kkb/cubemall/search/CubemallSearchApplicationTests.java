package com.kkb.cubemall.search;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
class CubemallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    void contextLoads() {
    }

}
