package com.kkb.cubemall.search.controller;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.search.model.SpuInfo;
import com.kkb.cubemall.search.repository.SpuInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("search/SpuInfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoRepository spuInfoRepository;

    @PostMapping("/save")
    public R save(@RequestBody SpuInfo spuInfo){
        spuInfoRepository.save(spuInfo);
        return R.ok();
    }

    @PostMapping("/saveAll")
    public R saveAll(@RequestBody List<SpuInfo> spuInfoList){
        spuInfoRepository.saveAll(spuInfoList);
        return R.ok();
    }


}
