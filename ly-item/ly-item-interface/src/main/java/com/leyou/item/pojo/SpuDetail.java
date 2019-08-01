package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetail {

    // 对应的SPU的id
    @Id
    private Long spuId;

    // 商品描述
    private String description;

    private String specialSpec;

    private String genericSpec;

    // 包装清单
    private String packingList;

    // 售后服务
    private String afterService;
}
