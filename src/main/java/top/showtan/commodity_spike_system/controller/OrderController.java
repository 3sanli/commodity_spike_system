package top.showtan.commodity_spike_system.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.entity.OrderInfo;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.redis.GoodsKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.service.GoodsService;
import top.showtan.commodity_spike_system.service.MiaoshaService;
import top.showtan.commodity_spike_system.service.OrderService;
import top.showtan.commodity_spike_system.service.UserService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.OrderSearchVo;
import top.showtan.commodity_spike_system.vo.OrderVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: sanli
 * @Date: 2019/4/1 17:07
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    MiaoshaController miaoshaController;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    UserService userService;

    //已优化
    @RequestMapping("/add")
    @AccessLimit(needLogin = true)
    public ResultUtil<OrderInfo> add(User user, Integer goodsId, Integer goodsCount) {
        //需要判断库存
        Integer stock = redisService.get(GoodsKeyPre.GOODS_STOCK, "" + goodsId, Integer.class);
        if (stock == null) {
            stock = goodsService.getStockById(goodsId);
        }
        if (stock - goodsCount < 0) {
            return ResultUtil.ERROR(CodeMsg.GOODS_STOCK_EMPTY);
        }
        GoodsVo byId = goodsService.getById(goodsId);
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(byId, orderInfo);
        orderInfo.setGoodsId(goodsId);
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsPrice(byId.getPrice());
        orderInfo.setGoodsCount(goodsCount);
        //预减库存
        orderService.decrStock(goodsId, goodsCount);
        orderService.doAdd(orderInfo);
        return ResultUtil.SUCCESS(orderInfo);
    }

    @RequestMapping("/cancel")
    @AccessLimit(needLogin = true)
    public ResultUtil<String> cancel(Integer orderId) {
        OrderVo order = orderService.getByOrderId(orderId);
        orderService.doCancel(orderId, order.getGoodsCount(), order.getGoodsId());
        return ResultUtil.SUCCESS("success");
    }

    @RequestMapping("/owner")
    @AccessLimit(needLogin = true)
    public ResultUtil<Map<String, Object>> ownerOrder(User user, @RequestParam(value = "page", required = true) Integer page,
                                                      @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {

        PageVo<OrderVo> pageVo = orderService.list(user.getId(), page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }

    @RequestMapping("/delete")
    @AccessLimit(needLogin = true)
    public ResultUtil<String> delete(Integer orderId) {
        orderService.deleteById(orderId);
        return ResultUtil.SUCCESS("success");
    }

    @RequestMapping("/search")
    @AccessLimit(needLogin = true)
    public ResultUtil<String> search(@RequestParam(value = "searchVo",required = false) String searchVo,
                                     @RequestParam(value = "page", required = true) Integer page,
                                     @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        OrderSearchVo orderSearchVo = JSON.parseObject(searchVo, OrderSearchVo.class);
        //查询汇总
        PageVo<OrderVo> pageVo = orderService.search(orderSearchVo, page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }
}
