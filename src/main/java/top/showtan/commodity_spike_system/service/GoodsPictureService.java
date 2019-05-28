package top.showtan.commodity_spike_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.showtan.commodity_spike_system.dao.GoodsPictureMapper;
import top.showtan.commodity_spike_system.entity.Picture;

/**
 * @Author: sanli
 * @Date: 2019/5/3 11:20
 */
@Service
public class GoodsPictureService {
    @Autowired
    GoodsPictureMapper goodsPictureMapper;
    public void deleteByGoodsId(Integer goodsId) {
        goodsPictureMapper.deleteByGoodsId(goodsId);
    }

    public void addGoodsPicture(Integer goodsId, Integer pictureId) {
        goodsPictureMapper.addGoodsPicture(goodsId,pictureId);

    }


    public void changePicture(Integer goodsId, Integer pictureId) {
        goodsPictureMapper.changePicture(goodsId,pictureId);
    }

    public Picture getByGoodsId(Integer goodsId) {
        return goodsPictureMapper.getByGoodsId(goodsId);
    }
}
