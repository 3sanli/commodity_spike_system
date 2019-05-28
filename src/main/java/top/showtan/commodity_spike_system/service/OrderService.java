package top.showtan.commodity_spike_system.service;

import jdk.nashorn.internal.objects.annotations.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.dao.OrderMapper;
import top.showtan.commodity_spike_system.entity.OrderInfo;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.redis.GoodsKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.vo.OrderSearchVo;
import top.showtan.commodity_spike_system.vo.OrderVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/4/1 20:03
 */
@Transactional
@Service
public class OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    MiaoshaService miaoshaService;

    public void doAdd(OrderInfo orderInfo) {
        if (orderInfo == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        //添加订单
        orderMapper.add(orderInfo);
    }

    public void doCancel(Integer orderId, Integer goodsCount, Integer goodsId) {
        if (orderId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        orderMapper.cancel(orderId);
        //增加库存
        addStock(goodsId, goodsCount);
    }

    public void decrStock(Integer goodsId, Integer goodsCount) {
        redisService.decrBy(GoodsKeyPre.GOODS_STOCK, "" + goodsId, (long) goodsCount);
        goodsService.decrStockBy(goodsId, goodsCount);
        //删除缓存中商品信息
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goodsId);
    }

    private void addStock(Integer goodsId, Integer goodsCount) {
        Integer stock = redisService.get(GoodsKeyPre.GOODS_STOCK, "" + goodsId, Integer.class);
        if (stock == null) {
            stock = goodsService.getStockById(goodsId);
            redisService.set(GoodsKeyPre.GOODS_STOCK, "" + goodsId, stock);
        } else {
            redisService.incrBy(GoodsKeyPre.GOODS_STOCK, "" + goodsId, (long) goodsCount);
        }
        goodsService.addStock(goodsId, goodsCount);
        //删除缓存中商品信息
        redisService.delete(GoodsKeyPre.GOODS_INFO, "" + goodsId);
    }

    public void cleanAll() {
        orderMapper.cleanAll();
    }

    public Integer countsByGoodsId(Integer goodsId) {
        Integer orderNumber = orderMapper.countsSimpleByGoodsId(goodsId);
        Integer miaoshaNumber = orderMapper.countMiaoshaByGoodsId(goodsId);
        return orderNumber == null ? miaoshaNumber == null ? 0 : miaoshaNumber : orderNumber + miaoshaNumber;
    }

    public PageVo<OrderVo> list(Integer userId, Integer page, Integer pageCount) {
        PageVo<OrderVo> pageVo = new PageVo<>();
        Long totalCounts = getCounts(userId);
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            return pageVo;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        List<OrderVo> orderVos = listByUserId(userId, pageVo.getOffset(), pageVo.getTake());


        pageVo.setData(orderVos);
        if (orderVos == null) {
            pageVo.setData(new ArrayList<>());
        }
        return pageVo;
    }


    private Long getCounts(Integer userId) {
        return orderMapper.getCountsByUserId(userId);
    }

    private List<OrderVo> listByUserId(Integer userId, Long offset, Integer take) {
        List<OrderVo> orderVos = orderMapper.listByUserId(userId, offset, take);
        for (OrderVo orderVo : orderVos) {
            if (orderVo.getMiaoshaOrderId() != null) {
                orderVo.setMiaosha(true);
            }
        }
        return orderVos;
    }

    public OrderVo getByOrderId(Integer orderId) {
        OrderVo res = orderMapper.getByOrderId(orderId);
        if (res.getMiaoshaOrderId() != null) {
            res.setMiaosha(true);
        }
        return res;
    }


    public void deleteById(Integer orderId) {
        if (orderId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        orderMapper.deleteById(orderId);
    }

    public PageVo<OrderVo> search(OrderSearchVo searchVo, Integer page, Integer pageCount) {
        PageVo<OrderVo> pageVo = new PageVo<>();
        Long totalCounts = getCounts(searchVo);
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            return pageVo;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        List<OrderVo> orderVos = listBySearchVo(searchVo, pageVo.getOffset(), pageVo.getTake());

        pageVo.setData(orderVos);
        if (orderVos == null) {
            pageVo.setData(new ArrayList<>());
        }

        return pageVo;
    }

    private Long getCounts(OrderSearchVo searchVo) {
        if (searchVo.getModel() == 1) {
            return orderMapper.getSimpleCountsBySearchVo(searchVo);
        } else {
            return orderMapper.getMiaoshaCountsBySearchVo(searchVo);
        }
    }

    private List<OrderVo> listBySearchVo(OrderSearchVo searchVo, Long offset, Integer take) {
        List<OrderVo> orderVos;
        if (searchVo.getModel() == 1) {
            orderVos = orderMapper.listSimpleBySearchVo(searchVo, offset, take);
        } else {
            orderVos = orderMapper.listMiaoshaBySearchVo(searchVo, offset, take);
        }
        setMiaosha(orderVos, searchVo.getModel());
        return orderVos;
    }

    private void setMiaosha(List<OrderVo> orderVos, Integer model) {
        for (OrderVo vo : orderVos) {
            if (model == 1) {
                vo.setMiaosha(false);
            } else {
                vo.setMiaosha(true);
            }
        }
    }

    public List<OrderVo> searchSimple() {
        return orderMapper.searchSimple();
    }

    public List<OrderVo> searchMiaosha() {
        return orderMapper.searchMiaosha();
    }
}
