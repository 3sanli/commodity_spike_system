package top.showtan.commodity_spike_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.showtan.commodity_spike_system.dao.GoodsCategoryMapper;

/**
 * @Author: sanli
 * @Date: 2019/5/3 11:16
 */
@Service
public class GoodsCategoryService {
    @Autowired
    GoodsCategoryMapper goodsCategoryMapper;

    public void deleteByGoodsId(Integer goodsId) {
        goodsCategoryMapper.deleteByGoodsId(goodsId);
    }

    public void addGoodsCategory(Integer goodsId, Integer categoryId) {
        goodsCategoryMapper.addGoodsCategory(goodsId, categoryId);
    }

    public void changeCategory(Integer goodsId, Integer categoryId) {
        goodsCategoryMapper.changeCategory(goodsId,categoryId);
    }
}
