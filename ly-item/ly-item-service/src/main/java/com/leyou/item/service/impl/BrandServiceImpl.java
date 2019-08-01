package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandDao;

    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 分页
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%").orEqualTo("letter", key.toUpperCase());
        }
        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        List<Brand> list = brandDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        // 解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);

        // 返回结果
        return new PageResult<>(info.getTotal(), list);
    }

    @Transactional
    @Override
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌
        brand.setId(null);
        int count = brandDao.insert(brand);
        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        // 新增中间表
        for (Long cid : cids) {
            count = brandDao.insertCategoryBrand(cid, brand.getId());
            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    @Override
    public Brand queryById(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandDao.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
