package com.kkb.cubemall.product.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SpuInfo {

    private Long id;

    private String spuName;

    private String spuDescription;

    private Long categoryId;

    private String categoryName;

    private Long brandId;

    private String brandName;

    private String brandImage;

    private Date updateTime;

    private String imgUrl;

    private Double price;
}
