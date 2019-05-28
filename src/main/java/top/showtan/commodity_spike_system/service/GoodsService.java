package top.showtan.commodity_spike_system.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.dao.GoodsMapper;
import top.showtan.commodity_spike_system.entity.Goods;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.redis.GoodsKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.vo.GoodsSearchVo;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:50
 */
@Transactional
@Service
public class GoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodsCategoryService goodsCategoryService;
    @Autowired
    GoodsPictureService goodsPictureService;

    public void add(Goods goods, Integer categoryId, Integer pictureId) {
        //删除缓存
        redisService.delete(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + goods.getCreatorId());
        //添加商品之后拿到自增id
        goodsMapper.add(goods);
        //添加商品分类映射
        goodsCategoryService.addGoodsCategory(goods.getId(), categoryId);
        //添加商品图片
        goodsPictureService.addGoodsPicture(goods.getId(), pictureId);
    }

    public GoodsVo getById(Integer goodsId) {
        //根据商品id拿到商品详情信息（用于商品修改回显以及商品信息展示）
        //先去redis中查，缓存优化
        String key = "" + goodsId;
        GoodsVo goodsVo = redisService.get(GoodsKeyPre.GOODS_INFO, key, GoodsVo.class);
        if (goodsVo == null) {
            goodsVo = goodsMapper.getById(goodsId);
            if (goodsVo == null) {
                throw new GlobalException(CodeMsg.GOODS_NOT_EXISTS);
            }
            redisService.set(GoodsKeyPre.GOODS_INFO, key, goodsVo);
        }
        return goodsVo;
    }

    public Integer getStockById(Integer goodsId) {
        if (goodsId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        Integer result = redisService.get(GoodsKeyPre.GOODS_STOCK, "" + goodsId, Integer.class);
        if (result != null) {
            return result;
        }
        result = goodsMapper.getStockById(goodsId);
        if (result == null) {
            return 0;
        }
        //放入缓存中
        redisService.set(GoodsKeyPre.GOODS_STOCK, "" + goodsId, result);
        return result;
    }

    public void modify(Goods goods) {
        //商家修改商品属性的时候要注意一致性问题，这个时候以数据库为准，先删除缓存中的值（后期缓存策略皆以此为准）
        String key = "" + goods.getId();
        redisService.delete(GoodsKeyPre.GOODS_INFO, key);
        redisService.delete(GoodsKeyPre.GOODS_STOCK, "" + goods.getId());
        //删除完毕后修改数据库中的相对应值，然后将新值放入缓存中
        goodsMapper.modify(goods);
    }


    public PageVo<GoodsVo> search(GoodsSearchVo goodsSearchVo, Integer page, Integer pageCount) {
        PageVo<GoodsVo> pageVo = new PageVo<>();
        Long totalCounts = getCounts(goodsSearchVo);
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            return pageVo;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        List<GoodsVo> goodsVos = listBySearchVo(goodsSearchVo, pageVo.getOffset(), pageVo.getTake());

        pageVo.setData(goodsVos);
        if (goodsVos == null) {
            pageVo.setData(new ArrayList<>());
        }

        return pageVo;
    }

    private List<GoodsVo> listBySearchVo(GoodsSearchVo searchVo, Long offset, Integer take) {
        List<GoodsVo> goodsVos;
        if (searchVo.getModel() == 1) {
            goodsVos = goodsMapper.listSimpleBySearchVo(searchVo, offset, take);
        } else {
            goodsVos = goodsMapper.listMiaoshaBySearchVo(searchVo, offset, take);
        }
        setMiaosha(goodsVos,searchVo.getModel());
        return goodsVos;
    }

    private void setMiaosha(List<GoodsVo> goodsVos, Integer model) {
        for(GoodsVo vo:goodsVos){
            if(model == 1){
                vo.setMiaosha(false);
            }else {
                vo.setMiaosha(true);
            }
        }
    }

    private Long getCounts(GoodsSearchVo searchVo) {
        if (searchVo.getModel() == 1) {
            return goodsMapper.getSimpleCountsBySearchVo(searchVo);
        } else {
            return goodsMapper.getMiaoshaCountsBySearchVo(searchVo);
        }
    }

    private Long getCounts(Integer userId) {
        return goodsMapper.getCountsByCreatorId(userId);
    }

    public void decrStockBy(Integer goodsId, Integer goodsCount) {
        if (goodsId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        goodsMapper.decrStockBy(goodsId, goodsCount);
    }


    public void addStock(Integer goodsId, Integer goodsCount) {
        if (goodsId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        goodsMapper.addStock(goodsId, goodsCount);
    }

    public void initAllGoodsStock() {
        goodsMapper.initAllGoodsStock();
    }

    /**
     * 由于并不需要模糊查询，可以用redis优化
     *
     * @param userId
     * @param page
     * @param pageCount
     * @return
     */
    public PageVo<GoodsVo> getByCreatorId(Integer userId, Integer page, Integer pageCount) {
        if (userId == null) {
            throw new GlobalException(CodeMsg.NEED_LOGIN);
        }
        //先走缓存
        PageVo<GoodsVo> pageVo = getCacheByLimit(userId, page, pageCount);
        if (pageVo != null) {
            return pageVo;
        }

        pageVo = new PageVo<>();
        Long totalCounts = getCounts(userId);
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            redisService.set(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + userId, pageVo);
            return pageVo;
        }

        //查出全部，将其放在redis中，后期全部走redis
        List<GoodsVo> goodsVos = goodsMapper.getAllByCreatorId(userId);

        pageVo.setData(goodsVos);
        if (goodsVos == null) {
            pageVo.setData(new ArrayList<>());
        }
        redisService.set(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + userId, pageVo);
        pageVo = getCacheByLimit(userId, page, pageCount);
        return pageVo;
    }

    private PageVo<GoodsVo> getCacheByLimit(Integer userId, Integer page, Integer pageCount) {
        PageVo<GoodsVo> pageVo = redisService.get(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + userId, PageVo.class);
        if (pageVo == null || pageVo.getDataCounts() == null || pageVo.getDataCounts() == 0) {
            return null;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        //封装查询条件
        Integer fromIndex = pageVo.getOffset().intValue();
        Integer toIndex = (fromIndex + pageVo.getTake()) >= pageVo.getDataCounts().intValue() ? (pageVo.getDataCounts().intValue()) : (fromIndex + pageVo.getTake());

        List<GoodsVo> goodsVos = JSON.parseArray(JSON.toJSONString(pageVo.getData().subList(fromIndex, toIndex)), GoodsVo.class);
        pageVo.setData(goodsVos);
        return pageVo;
    }

    public void deleteById(Integer goodsId, Integer userId) {
        //删除商品之前需要判断是否存在有秒杀活动关联商品 以及是否有相关订单关联该商品
        if (!isExistMiaoshaGoods(goodsId) && !isExistOrder(goodsId)) {
            //先删除该商品所有分类信息
            goodsCategoryService.deleteByGoodsId(goodsId);
            //再删除图片信息
            goodsPictureService.deleteByGoodsId(goodsId);
            goodsMapper.deleteById(goodsId);
            redisService.delete(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + userId);
            redisService.delete(GoodsKeyPre.GOODS_STOCK, "" + goodsId);
            redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goodsId);
        }
    }

    private boolean isExistOrder(Integer goodsId) {
        Integer number = orderService.countsByGoodsId(goodsId);
        if (number == null || number == 0) {
            return false;
        }
        throw new GlobalException(CodeMsg.EXIST_ORDER);
    }

    private boolean isExistMiaoshaGoods(Integer goodsId) {
        Integer number = miaoshaService.countsByGoodsId(goodsId);
        if (number == null || number == 0) {
            return false;
        }
        throw new GlobalException(CodeMsg.EXIST_MIAOSHA);
    }

    public void modify(Goods goods, Integer categoryId, Integer pictureId) {
        redisService.delete(GoodsKeyPre.OWNER_SIMAPLE_GOODS, "" + goods.getCreatorId());
        goodsMapper.modify(goods);
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goods.getId());
        redisService.delete(GoodsKeyPre.GOODS_STOCK, "" + goods.getId());
        goodsCategoryService.changeCategory(goods.getId(), categoryId);
        goodsPictureService.changePicture(goods.getId(), pictureId);
    }
}
