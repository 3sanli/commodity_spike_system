package top.showtan.commodity_spike_system.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.dao.MiaoshaMapper;
import top.showtan.commodity_spike_system.entity.MiaoshaGoods;
import top.showtan.commodity_spike_system.entity.MiaoshaOrder;
import top.showtan.commodity_spike_system.entity.OrderInfo;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.message.MiaoshaMessage;
import top.showtan.commodity_spike_system.redis.GoodsKeyPre;
import top.showtan.commodity_spike_system.redis.MiaoshaKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.OrderVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/26 19:41
 */
@Transactional
@Service
public class MiaoshaService {
    @Autowired
    MiaoshaMapper miaoshaMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    public MiaoshaGoods setMiaosha(MiaoshaGoods miaoshaGoods, Integer userId) {
        miaoshaMapper.add(miaoshaGoods);
        redisService.delete(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId);
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + miaoshaGoods.getGoodsId());
        return miaoshaGoods;
    }

    public void modifyMiaosha(MiaoshaGoods miaoshaGoods, Integer userId) {
        miaoshaMapper.modify(miaoshaGoods);
        redisService.delete(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId);
        redisService.delete(MiaoshaKeyPre.MIAOSHA_STOCK, "" + miaoshaGoods.getGoodsId());
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + miaoshaGoods.getGoodsId());
    }

    public PageVo<GoodsVo> search(Integer page, Integer pageCount) {
        PageVo<GoodsVo> pageVo = new PageVo<>();
        Long totalCounts = getCounts();
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            return pageVo;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        List<GoodsVo> goodsVos = miaoshaMapper.search(pageVo.getOffset(), pageVo.getTake());

        pageVo.setData(goodsVos);
        if (goodsVos == null) {
            pageVo.setData(new ArrayList<>());
        }

        return pageVo;
    }

    private Long getCounts() {
        return miaoshaMapper.countsAll();
    }

    public void doMiaosha(MiaoshaMessage miaoshaMessage) {
        User miaoshaUser = miaoshaMessage.getUser();
        GoodsVo miaoshaGoods = miaoshaMessage.getGoodsVo();
        //判断是否已经秒杀
        boolean isRepeat = isRepeat(miaoshaUser.getId(), miaoshaGoods.getId());
        if (isRepeat) {
            return;
        }
        boolean isRemain = isRemain(miaoshaGoods.getId());
        if (!isRemain) {
            return;
        }

        //生成普通订单，并返回订单id，此时并不走正常订单路线，因为是两个库存
        OrderInfo orderInfo = createOrderInfo(miaoshaGoods, miaoshaUser);
        orderService.doAdd(orderInfo);
        //生成秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder(miaoshaUser.getId(), miaoshaGoods.getId(), orderInfo.getId());
        miaoshaMapper.doAdd(miaoshaOrder);
        OrderVo vo = new OrderVo();
        BeanUtils.copyProperties(orderInfo, vo);
        redisService.set(MiaoshaKeyPre.MIAOSHA_REPEAT, "" + miaoshaUser.getId() + "_" + miaoshaGoods.getId(), vo);
        //减少相关库存
        decrStock(miaoshaGoods.getId());
    }

    private boolean isRepeat(Integer userId, Integer goodsId) {
        OrderVo orderVo = miaoshaMapper.getByUserIdAndGoodsId(userId, goodsId);
        return orderVo != null;
    }

    private boolean isRemain(Integer goodsId) {
        Integer miaoshaStock = miaoshaMapper.getStockByGoodsId(goodsId);
        if (miaoshaStock == null) {
            return false;
        }
        return miaoshaStock > 0;
    }


    private OrderInfo createOrderInfo(GoodsVo vo, User miaoshaUser) {
        OrderInfo result = new OrderInfo();
        result.setGoodsId(vo.getId());
        result.setGoodsName(vo.getGoodsName());
        result.setGoodsPrice(vo.getMiaoshaPrice());
        result.setUserId(miaoshaUser.getId());
        result.setGoodsCount(1);
        return result;
    }

    public void decrStock(Integer goodsId) {
        redisService.decr(MiaoshaKeyPre.MIAOSHA_STOCK, "" + goodsId);
        miaoshaMapper.decrStock(goodsId);
        //删除缓存信息
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goodsId);
    }

    public GoodsVo getByGoodsId(Integer goodsId) {
        GoodsVo goodsVo = miaoshaMapper.getByGoodsId(goodsId);
        return goodsVo;
    }

    public Integer getStockByGoodsId(Integer goodsId) {
        Integer miaoshaStock = miaoshaMapper.getStockByGoodsId(goodsId);
        if (miaoshaStock != null) {
            redisService.set(MiaoshaKeyPre.MIAOSHA_STOCK, "" + goodsId, miaoshaStock);
        }
        return miaoshaStock;
    }

    public void cleanAll() {
        miaoshaMapper.cleanAll();
    }

    public void initAllMiaoshaGoodsStock() {
        miaoshaMapper.initAllMiaoshaGoodsStock();
    }

    public OrderVo getByUserIdAndGoodsId(Integer userId, Integer goodsId) {
        OrderVo orderVo = miaoshaMapper.getByUserIdAndGoodsId(userId, goodsId);
        if (orderVo != null) {
            redisService.set(MiaoshaKeyPre.MIAOSHA_REPEAT, "" + userId + "_" + goodsId, orderVo);
        }
        return orderVo;
    }


    public List<GoodsVo> getAllStock() {
        return miaoshaMapper.getAllStock();
    }

    public Integer getResultByUserIdAndGoodsId(Integer userId, Integer goodsId) {
        boolean repeat = isRepeat(userId, goodsId);
        if (!repeat) {
            if (!isRemain(goodsId)) {
                return 1;
            } else {
                return 2;
            }
        }
        return 0;
    }

    public Integer countsByGoodsId(Integer goodsId) {
        return miaoshaMapper.countsByGoodsId(goodsId);
    }

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
            redisService.set(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId, pageVo);
            return pageVo;
        }

        //查出全部，将其放在redis中，后期全部走redis
        List<GoodsVo> goodsVos = miaoshaMapper.getAllByCreatorId(userId);

        pageVo.setData(goodsVos);
        if (goodsVos == null) {
            pageVo.setData(new ArrayList<>());
        }
        redisService.set(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId, pageVo);
        pageVo = getCacheByLimit(userId, page, pageCount);
        return pageVo;
    }

    private PageVo<GoodsVo> getCacheByLimit(Integer userId, Integer page, Integer pageCount) {
        PageVo<GoodsVo> pageVo = redisService.get(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId, PageVo.class);
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

    private Long getCounts(Integer goodsId) {
        return miaoshaMapper.getCountsByCreatorId(goodsId);
    }


    public void over(Integer userId, Integer goodsId) {
        miaoshaMapper.over(goodsId);
        redisService.delete(MiaoshaKeyPre.OWNER_MIAOSHA_GOODS, "" + userId);
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goodsId);
        redisService.delete(MiaoshaKeyPre.MIAOSHA_STOCK, "" + goodsId);
    }
}
