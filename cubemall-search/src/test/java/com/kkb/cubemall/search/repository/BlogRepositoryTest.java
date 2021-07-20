package com.kkb.cubemall.search.repository;

import com.kkb.cubemall.search.model.SpuInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlogRepositoryTest {

    @Autowired
    SpuInfoRepository spuInfoRepository;

    @Test
    void setMappings() {

        SpuInfo spuInfo=new SpuInfo();
        spuInfo.setSpuName("瑞动（SWISSMOBILITY）拉杆箱30英寸差旅大容量行李箱 防泼水静音万向轮旅行箱男女 5560黑色");
        spuInfo.setSpuDescription("★3日-6日10点限时闪购，直降立省60，到手价269！★30英寸热卖爆款，超大容量超高性价比！点击进入闪购专场");
        spuInfo.setCategoryId(758l);
        spuInfo.setCategoryName("半身裙");
        spuInfo.setBrandId(21990l);
        spuInfo.setBrandName("淘宝");
        spuInfo.setUpdateTime(new Date());
        spuInfo.setImgUrl("https://img20.360buyimg.com/vc/jfs/t2200/96/1372883847/49747/1d03a9ce/5695c894Ne966c845.jpg");
        spuInfo.setPrice(new Double(234234));
        spuInfo.setId(0l);

        spuInfoRepository.saveAll(Arrays.asList(spuInfo));

    }
}