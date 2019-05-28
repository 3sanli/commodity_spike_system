package top.showtan.commodity_spike_system.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.entity.Goods;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.service.GoodsService;
import top.showtan.commodity_spike_system.service.PictrueService;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.vo.GoodsSearchVo;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: sanli
 * @Date: 2019/3/25 22:15
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    PictrueService pictrueService;


    //已优化
    @RequestMapping("/add")
    @AccessLimit(needLogin = true)
    public ResultUtil<Goods> addGoods(User user, GoodsVo goodsVo) {
        //添加商品
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsVo, goods);
        goods.setCreatorId(user.getId());
        goods.setCreatorName(user.getNickName());
        pictrueService.add(goodsVo.getGoodsPicture());
        goodsService.add(goods, goodsVo.getCategoryId(), goodsVo.getGoodsPicture().getId());
        return ResultUtil.SUCCESS(goods);
    }

    //已优化
    @RequestMapping("/get")
    public ResultUtil<GoodsVo> getGoods(@Validated Integer goodsId) {
        //查询指定id商品
        GoodsVo goodsVo = goodsService.getById(goodsId);
        renderMiaoshaView(goodsVo);
        return ResultUtil.SUCCESS(goodsVo);
    }

    private void renderMiaoshaView(GoodsVo goodsVo) {
        //不是秒杀活动
        if (goodsVo.getStartTime() == null) {
            return;
        }
        long currTime = System.currentTimeMillis();
        if (goodsVo.getMiaoshaStock() <= 0) {   //库存不足情况一定秒杀结束
            goodsVo.setOver(true);
            goodsVo.setStatus(-1);
        } else if (currTime - goodsVo.getEndTime().getTime() >= 0) {    //库存足够但是过了活动时间
            goodsVo.setOver(true);
            goodsVo.setStatus(-1);
        } else if (currTime - goodsVo.getStartTime().getTime() >= 0) {
            goodsVo.setStatus(1);
            goodsVo.setOver(false);
        }
        long remainTime = goodsVo.getStartTime().getTime() - currTime;
        if (remainTime > 0) {
            goodsVo.setRemainTime(remainTime);
            goodsVo.setStatus(0);
        }
    }

    //已优化
    @RequestMapping("/modify")
    @AccessLimit(needLogin = true)
    public ResultUtil<GoodsVo> modifyGoods(User user, GoodsVo goodsVo) {
        //修改商品属性
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsVo, goods);
        goods.setCreatorId(user.getId());
        pictrueService.add(goodsVo.getGoodsPicture());
        goodsService.modify(goods, goodsVo.getCategoryId(), goodsVo.getGoodsPicture().getId());
        return ResultUtil.SUCCESS(goodsVo);
    }

    @RequestMapping("/search")
    public ResultUtil<Map<String, Object>> search(@RequestParam(value = "searchVo", required = false) String goodsSearchVo,
                                                  @RequestParam(value = "page", required = true) Integer page,
                                                  @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        GoodsSearchVo searchVo;
        if (goodsSearchVo == null) {
            searchVo = new GoodsSearchVo();
        } else {
            searchVo = JSON.parseObject(goodsSearchVo, GoodsSearchVo.class);
        }
        if (searchVo.getModel() == null) {
            searchVo.setModel(1);
        }
        //查询汇总
        PageVo<GoodsVo> pageVo = goodsService.search(searchVo, page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }

    //已优化
    @RequestMapping("/owner")
    @ResponseBody
    @AccessLimit(needLogin = true)
    public ResultUtil<Map<String, Object>> ownerGoodsList(User user,
                                                          @RequestParam(value = "page", required = true) Integer page,
                                                          @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        PageVo<GoodsVo> pageVo = goodsService.getByCreatorId(user.getId(), page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }

    //已优化
    @RequestMapping("/delete")
    @ResponseBody
    @AccessLimit(needLogin = true)
    public ResultUtil<String> delete(User user, Integer goodsId) {
        goodsService.deleteById(goodsId, user.getId());
        return ResultUtil.SUCCESS("success");
    }
}
