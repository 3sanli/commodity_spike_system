package top.showtan.commodity_spike_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.dao.CategoryMapper;
import top.showtan.commodity_spike_system.entity.Category;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.redis.CategoryKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.vo.CategoryAddVo;
import top.showtan.commodity_spike_system.vo.CategoryGetVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/26 21:14
 */
@Service
@Transactional
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    RedisService redisService;

    public List<Category> getAllByParentId(Integer categoryId) {
        if (categoryId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        List<Category> allByParentId = categoryMapper.getAllByParentId(categoryId);
        if (allByParentId == null) {
            return new ArrayList<>();
        }
        return allByParentId;
    }

    public List<Category> getAllInited() {
        List<Category> allInited = categoryMapper.getAllInited();
        if (allInited == null) {
            return new ArrayList<>();
        }
        return allInited;
    }

    public void modifyById(CategoryGetVo categoryGetVo) {
        if (categoryGetVo == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        categoryMapper.modifyById(categoryGetVo);
    }

    public void deleteByIdList(List<Integer> categoryIds) {
        //自定义级联删除，先删除所有子分类，再删除本类，有点复杂，通过数据库实现
        if (categoryIds == null) {
            return;
        }
        categoryMapper.deleteByIdList(categoryIds);
    }

    public void add(CategoryAddVo categoryAddVo) {
        if (categoryAddVo == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        Integer parentId = categoryAddVo.getParentId();
        if (parentId != null) {
            parentNode(parentId);
        }
        categoryMapper.add(categoryAddVo);
    }

    private void parentNode(Integer parentId) {
        //先判断redis中该分类是否为直接父分类
        Boolean isParent = redisService.get(CategoryKeyPre.IS_PARENT, "" + parentId, boolean.class);
        if (isParent == null || !isParent) {
            categoryMapper.parentNode(parentId);
            redisService.set(CategoryKeyPre.IS_PARENT, "" + parentId, true);
        }
    }
}
