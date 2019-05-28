package top.showtan.commodity_spike_system.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.showtan.commodity_spike_system.entity.Goods;
import top.showtan.commodity_spike_system.entity.MiaoshaGoods;
import top.showtan.commodity_spike_system.redis.GoodsKeyPre;
import top.showtan.commodity_spike_system.redis.MiaoshaKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.service.GoodsService;
import top.showtan.commodity_spike_system.service.MiaoshaService;
import top.showtan.commodity_spike_system.service.OrderService;
import top.showtan.commodity_spike_system.service.UserService;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.OrderVo;

import java.util.Date;
import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/4/12 13:40
 */
@RestController
@RequestMapping("/init")
public class InitController implements InitializingBean {
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    UserService userService;
    @Autowired
    UserController userController;

    private static Integer TEST_SIMPLE_GOODS_ID = 1;
    private static final String TEST_SIMPLE_STOCK = "996";

    private static Integer TEST_MIAOSHA_GOODS_ID = 1;
    private static final String TEST_MIAOSHA_STOCK = "996";


    @Override
    public void afterPropertiesSet() throws Exception {
        iniStock();
    }

    public void iniStock() {
        //获取所有秒杀商品的id和库存
        List<GoodsVo> goodsVos = miaoshaService.getAllStock();
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(MiaoshaKeyPre.MIAOSHA_STOCK, "" + goodsVo.getId(), goodsVo.getMiaoshaStock());
        }
    }

    @RequestMapping("/do")
    public ResultUtil<String> init(@RequestParam(value = "simpleStock", defaultValue = TEST_SIMPLE_STOCK) Integer simpleStock,
                                   @RequestParam(value = "miaoshaStock", defaultValue = TEST_MIAOSHA_STOCK) Integer miaoshaStock) {
        //清除订单缓存
        cleanRedisData();
        //清除所有普通订单和秒杀订单

        miaoshaService.cleanAll();
        orderService.cleanAll();

        //初始化测试数据库存
        initSimpleGoodsInfo(TEST_SIMPLE_GOODS_ID, simpleStock);
        initMiaoshaGoodsInfo(TEST_MIAOSHA_GOODS_ID, miaoshaStock);
        return ResultUtil.SUCCESS("success");
    }

    private void cleanRedisData() {
        //删除redis中goodsStock和miaoshaStock以及repeat
        //先获取所有普通订单，秒杀订单
        List<OrderVo> simples = orderService.searchSimple();
        List<OrderVo> miaoshas = orderService.searchMiaosha();
        for (OrderVo vo : simples) {
            redisService.set(GoodsKeyPre.GOODS_STOCK, "" + vo.getGoodsId(), TEST_SIMPLE_STOCK);
        }
        for (OrderVo vo : miaoshas) {
            redisService.set(MiaoshaKeyPre.MIAOSHA_STOCK, "" + vo.getGoodsId(), TEST_MIAOSHA_STOCK);
            redisService.delete(MiaoshaKeyPre.MIAOSHA_REPEAT, "" + vo.getUserId() + "_" + vo.getGoodsId());
        }
    }

    private void initMiaoshaGoodsInfo(Integer goodsId, Integer miaoshaStock) {
        GoodsVo byGoodsId = miaoshaService.getByGoodsId(goodsId);
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        BeanUtils.copyProperties(byGoodsId, miaoshaGoods);

        miaoshaGoods.setGoodsId(byGoodsId.getId());
        long currentTimeMillis = System.currentTimeMillis();
        long tommrowTimeMillis = currentTimeMillis + 86400000;
        miaoshaGoods.setStartTime(new Date(currentTimeMillis));
        miaoshaGoods.setEndTime(new Date(tommrowTimeMillis));
        miaoshaGoods.setMiaoshaStock(miaoshaStock);
        miaoshaService.modifyMiaosha(miaoshaGoods, byGoodsId.getCreatorId());
    }

    private void initSimpleGoodsInfo(Integer goodsId, Integer simpleStock) {
        GoodsVo simpleGoods = goodsService.getById(goodsId);
        simpleGoods.setStock(simpleStock);
        Goods goods = new Goods();
        BeanUtils.copyProperties(simpleGoods, goods);
        goodsService.modify(goods, simpleGoods.getCategoryId(), simpleGoods.getGoodsPicture().getId());
    }
}
