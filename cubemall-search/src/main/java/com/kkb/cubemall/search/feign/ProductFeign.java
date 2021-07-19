package com.kkb.cubemall.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("cubemall-search")
public class ProductFeign {



}
