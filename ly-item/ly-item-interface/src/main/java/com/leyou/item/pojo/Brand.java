package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_brand")
public class Brand {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    // 品牌名称
    private String name;

    // 品牌图片
    private String image;

    // 品牌首字母
    private Character letter;
}
