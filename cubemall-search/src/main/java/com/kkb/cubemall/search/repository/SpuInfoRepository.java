package com.kkb.cubemall.search.repository;


import com.kkb.cubemall.search.model.SpuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: sublun
 * @Date: 2021/4/26 10:58
 */
public interface SpuInfoRepository extends ElasticsearchRepository<SpuInfo, Long> {

}
