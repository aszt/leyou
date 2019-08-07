package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult extends PageResult<Goods> {

    private List<Brand> brands;

    private List<Category> categories;

    public SearchResult() {
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Brand> brands, List<Category> categories) {
        super(total, totalPage, items);
        this.brands = brands;
        this.categories = categories;
    }
}
