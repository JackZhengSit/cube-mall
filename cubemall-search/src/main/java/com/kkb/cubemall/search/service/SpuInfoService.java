package com.kkb.cubemall.search.service;

import com.kkb.cubemall.common.utils.R;

import java.util.Map;

/**
 * @Author: sublun
 * @Date: 2021/4/27 11:48
 */
public interface SpuInfoService {

    Map<String, Object> search(Map<String,String> paramMap);
}
