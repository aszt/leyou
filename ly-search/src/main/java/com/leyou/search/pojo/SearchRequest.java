package com.leyou.search.pojo;

public class SearchRequest {

    // 搜索条件
    private String key;

    // 当前页
    private Integer page;

    // 默认页
    private static final int DEFAULT_PAGE = 1;

    // 每页大小
    private static final int DEFAULT_SIZE = 20;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
