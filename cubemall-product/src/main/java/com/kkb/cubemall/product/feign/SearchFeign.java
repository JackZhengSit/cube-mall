package com.kkb.cubemall.product.feign;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.dto.SpuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("cubemall-search")
public interface SearchFeign {
    @PostMapping("/search/SpuInfo/save")
    public R save(SpuInfo spuInfo);

    @PostMapping("/search/SpuInfo/saveAll")
    public R saveAll(List<SpuInfo> spuInfoList);
}
