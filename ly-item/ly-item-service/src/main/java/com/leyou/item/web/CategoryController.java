package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询商品分类
     *
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid") Long pid) {
        // 旧写法
//        ResponseEntity.status(HttpStatus.OK).body(null);
        // 新写法
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 根据id查询商品分类
     *
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     *
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id) {
        return ResponseEntity.ok(categoryService.queryAllByCid3(id));
    }
}
