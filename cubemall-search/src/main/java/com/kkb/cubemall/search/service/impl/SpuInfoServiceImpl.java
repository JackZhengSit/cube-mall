package com.kkb.cubemall.search.service.impl;


import com.kkb.cubemall.search.model.SpuInfo;
import com.kkb.cubemall.search.repository.SpuInfoRepository;
import com.kkb.cubemall.search.service.SpuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: sublun
 * @Date: 2021/4/27 11:50
 */
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    private SpuInfoRepository spuInfoRepository;
    @Autowired
    private ElasticsearchRestTemplate template;


    @Override
    public Map<String, Object> search(Map<String, String> paramMap) {
        //1）接收controller传递的参数
        //2）跟参数封装查询条件
        // 根据关键词查询
        //品牌过滤
        // 分类过滤
        //价格区间过滤
        // 排序
        //分页
        //高亮
        //聚合条件：品牌、分类
        NativeSearchQuery query = buildQuery(paramMap);
        //3）执行查询
        SearchHits<SpuInfo> searchHits = template.search(query, SpuInfo.class);
        //4）取返回结果
        Map<String, Object> resultMap = parseResponse(searchHits);
        //计算页面
        long totalHits = searchHits.getTotalHits();
        //取当前页码
        String pageNumStr = paramMap.get("pageNum");
        int pageNum = StringUtils.isNotBlank(pageNumStr)?Integer.parseInt(pageNumStr):1;
        resultMap.put("pageNum", pageNum);
        //计算总页数
        int totalPages = (int) Math.ceil(totalHits / 60);
        resultMap.put("totalPages", totalPages);
        return resultMap;
    }

    private NativeSearchQuery buildQuery(Map<String,String> paramMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        String keywords = paramMap.get("keywords");
        if (keywords == null) {
            keywords = "";
        }
        // 根据关键词查询
        builder.withQuery(QueryBuilders.matchQuery("spuName", keywords));
        //品牌过滤
        String brand = paramMap.get("brand");
        if (StringUtils.isNotBlank(brand)) {
            builder.withFilter(QueryBuilders.termQuery("brandName", brand));
        }
        // 分类过滤
        String category = paramMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            builder.withFilter(QueryBuilders.termQuery("categoryName", category));
        }
        //价格区间过滤
        String price = paramMap.get("price");
        if (StringUtils.isNotBlank(price)) {
            String[] split = price.split("-");
            builder.withFilter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));
        }
        // 排序
        String sortRule = paramMap.get("sortRule");
        String sortField = paramMap.get("sortField");
        if (StringUtils.isNotBlank(sortRule)
                && StringUtils.isNotBlank(sortField)) {
            if ("ASC".equals(sortRule)) {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
            } else {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
            }
        }
        //分页
        String pageNumString = paramMap.get("pageNum");
        int pageNum = 1;
        if (StringUtils.isNotBlank(pageNumString)) {
            pageNum = Integer.parseInt(pageNumString);
        }
        builder.withPageable(PageRequest.of(pageNum - 1, 60));
        //高亮
        builder.withHighlightBuilder(new HighlightBuilder()
                .field("spuName")
                .preTags("<em style=\"color:red\">")
                .postTags("</em>"));
        //聚合条件：品牌、分类
        builder.addAggregation(AggregationBuilders.terms("brandGroup").field("brandName"));
        builder.addAggregation(AggregationBuilders.terms("categoryGroup").field("categoryName"));

        return builder.build();
    }

    private Map<String, Object> parseResponse(SearchHits<SpuInfo> searchHits) {
        Map<String, Object> resultMap = new HashMap<>();
        //总记录数
        long totalHits = searchHits.getTotalHits();
        resultMap.put("totalRows", totalHits);
        //商品列表
        List<SpuInfo> spuInfoList = searchHits.getSearchHits().stream()
                .map(e -> {
                    SpuInfo spuInfo = e.getContent();
                    List<String> highlightField = e.getHighlightField("spuName");
                    if (highlightField.size() > 0) {
                        spuInfo.setSpuName(highlightField.get(0));
                    }
                    return spuInfo;
                })
                .collect(Collectors.toList());
        resultMap.put("rows", spuInfoList);
        //聚合结果
        Aggregations aggregations = searchHits.getAggregations();
        //取品牌列表
        ParsedStringTerms brandGroup = aggregations.get("brandGroup");
        List<String> brandList = brandGroup.getBuckets().stream()
                .map(b -> b.getKeyAsString())
                .collect(Collectors.toList());
        resultMap.put("brandList", brandList);
        //取分类列表
        ParsedStringTerms categoryGroup = aggregations.get("categoryGroup");
        List<String> categoryList = categoryGroup.getBuckets().stream()
                .map(b -> b.getKeyAsString())
                .collect(Collectors.toList());
        resultMap.put("categoryList", categoryList);
        return resultMap;
    }
}
