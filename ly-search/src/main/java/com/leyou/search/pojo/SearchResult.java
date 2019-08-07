package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods> {

    // 品牌待选项
    private List<Brand> brands;

    // 分类待选项
    private List<Category> categories;

    // 规格参数key及待选项
    private List<Map<String, Object>> specs;

    public SearchResult() {
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Brand> brands, List<Category> categories, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.brands = brands;
        this.categories = categories;
        this.specs = specs;
    }
}
