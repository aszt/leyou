package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;

public interface GoodsService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveGoods(Spu spu);
}
