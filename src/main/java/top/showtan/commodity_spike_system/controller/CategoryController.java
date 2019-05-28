package top.showtan.commodity_spike_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.entity.Category;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.service.CategoryService;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.vo.CategoryAddVo;
import top.showtan.commodity_spike_system.vo.CategoryGetVo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/26 21:12
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping("/add")
    @AccessLimit(needLogin = true)
    public ResultUtil<Category> add(User user, @Validated CategoryAddVo categoryAddVo) {
        categoryService.add(categoryAddVo);
        return ResultUtil.SUCCESS(categoryAddVo);
    }

    @RequestMapping("/init")
    @AccessLimit(needLogin = true)
    public ResultUtil<List<Category>> initCategory(User user) {
        //初始化商品分类列表所有父分类
        List<Category> categories = categoryService.getAllInited();
        return ResultUtil.SUCCESS(categories);
    }

    @RequestMapping("/list")
    @AccessLimit(needLogin = true)
    public ResultUtil<List<Category>> listCategory(User user, @NotNull Integer categoryId) {
        //根据id查询其下所有分类
        List<Category> categories = categoryService.getAllByParentId(categoryId);
        return ResultUtil.SUCCESS(categories);
    }

    @RequestMapping("/modify")
    @AccessLimit(needLogin = true)
    public ResultUtil<Category> modify(User user, @Validated CategoryGetVo categoryGetVo) {
        //修改分类名字
        categoryService.modifyById(categoryGetVo);
        return ResultUtil.SUCCESS(categoryGetVo);
    }

    @RequestMapping("/delete")
    @AccessLimit(needLogin = true)
    public ResultUtil<List<Integer>> delete(User user, @RequestBody List<Integer> categoryIds) {
        //删除指定id的分类，若其为父类，则连带删除其所有子类
        categoryService.deleteByIdList(categoryIds);
        return ResultUtil.SUCCESS(categoryIds);
    }
}
