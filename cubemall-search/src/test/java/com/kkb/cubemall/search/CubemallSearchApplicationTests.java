package com.kkb.cubemall.search;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class CubemallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    void contextLoads() {
    }

}
