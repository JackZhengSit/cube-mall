package com.kkb.cubemall.product.service.impl;

import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.dto.SpuInfo;
import com.kkb.cubemall.product.entity.*;
import com.kkb.cubemall.product.feign.SearchFeign;
import com.kkb.cubemall.product.service.*;
import com.kkb.cubemall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuInfoDao spuInfoDao;

    @Autowired
    private SearchFeign searchFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * ???7?????????????????????????????????
     * @param spuSaveVo
     */
    @Transactional(rollbackFor = MethodArgumentNotValidException.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1.??????spu????????????: tb_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());

        this.saveBaseSpuInfo(spuInfoEntity);

        //2.??????spu???????????????: tb_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(spuSaveVo.getDecript());

        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.??????spu?????????: tb_spu_images
        List<String> images = spuSaveVo.getImages();

        spuImagesService.saveImage(spuInfoEntity.getId(), images);

        //4.??????spu???????????????: tb_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrId((long) attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(collect);

        //5.????????????spu??????????????????sku??????
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size()>0) {
            skus.forEach(item->{
                //??????????????????
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //5.1 ??????sku???????????????:tb_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCategoryId(spuInfoEntity.getCategoryId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.saveSkuInfo(skuInfoEntity);

                //5.2 ??????sku???????????????:tb_sku_images
                Long skuId = skuInfoEntity.getId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    return StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(imagesEntities);

                //5.3 ??????sku???????????????: tb_sku_sale_attr_value
                List<Attr> attrs = item.getAttr();
                if (attrs != null && attrs.size() > 0) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());

                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                }
            });
        }
    }

    /**
     * spu???????????????
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByConditon(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //????????????key
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        //??????????????????id,??????id
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String categoryId = (String) params.get("categoryId");
        if (!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)) {
            wrapper.eq("category_id", categoryId);
        }

        //????????????status
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("public_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void putOnSale(Long spuId) {
        //1.??????spuID??????????????????????????????
        SpuInfo spuInfo = spuInfoDao.getSpuInfoById(spuId);
        //2.??????????????????
        SpuInfoEntity entity = new SpuInfoEntity();
        entity.setId(spuId);
        entity.setPublishStatus(1);
        baseMapper.updateById(entity);
        //3.??????ElasticSearchRepository????????????????????????????????????
        searchFeign.save(spuInfo);
    }

    @Override
    public R syncSpuInfo() {
        //1???????????????????????????????????????
        List<SpuInfo> infoList = spuInfoDao.getSpuInfoList();
        //2???????????????????????????ES???
        searchFeign.saveAll(infoList);
        //????????????
        return R.ok();
    }

    /**
     * 1.??????spu????????????: tb_spu_info
     * @param spuInfoEntity
     */
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}