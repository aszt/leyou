package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryDao;

    @Override
    public List<Category> queryCategoryListByPid(Long pid) {
        // 查询条件，mapper会把对象中的非空属性作为查询条件
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryDao.select(t);
        // 判断结果
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return list;
    }

    @Override
    public List<Category> queryByIds(List<Long> ids) {
        List<Category> list = categoryDao.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return list;
    }

    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryDao.selectByPrimaryKey(id);
        Category c2 = this.categoryDao.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryDao.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1, c2, c3);
    }
}
