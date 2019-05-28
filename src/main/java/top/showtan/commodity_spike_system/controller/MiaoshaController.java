package top.showtan.commodity_spike_system.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.entity.MiaoshaGoods;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.message.MiaoshaMessage;
import top.showtan.commodity_spike_system.rabbit.MiaoshaSender;
import top.showtan.commodity_spike_system.redis.MiaoshaKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.redis.VerificationCodeKey;
import top.showtan.commodity_spike_system.service.MiaoshaService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.ExcuteCmdUtil;
import top.showtan.commodity_spike_system.util.MD5Util;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.PageVo;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * @Author: sanli
 * @Date: 2019/3/26 19:55
 */
@RestController
@RequestMapping("/miaosha")
public class MiaoshaController {
    public static final String CODE_RESULT_KEY = "userId:%s,goodsId:%s";
    public static final String MIAOSHA_URL = "userId:%s,goodsId:%s";
    public static final String MIAOSHA_LIMIT = "userId:%s,goodsId:%s";


    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MiaoshaSender miaoshaSender;

    @Autowired
    RedisService redisService;


    @RequestMapping("/add")
    @AccessLimit(needLogin = true)
    public ResultUtil<MiaoshaGoods> setMiaosha(User user, GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        BeanUtils.copyProperties(goodsVo, miaoshaGoods);
        miaoshaGoods.setGoodsId(goodsVo.getId());
        miaoshaService.setMiaosha(miaoshaGoods, user.getId());
        return ResultUtil.SUCCESS(miaoshaGoods);
    }


    @RequestMapping("/modify")
    @AccessLimit(needLogin = true)
    public ResultUtil<MiaoshaGoods> modifyMiaosha(User user, GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        BeanUtils.copyProperties(goodsVo, miaoshaGoods);
        miaoshaGoods.setGoodsId(goodsVo.getId());
        miaoshaService.modifyMiaosha(miaoshaGoods, user.getId());
        return ResultUtil.SUCCESS(miaoshaGoods);
    }

    @RequestMapping("/search")
    public ResultUtil<Map<String, Object>> search(@RequestParam(value = "page", required = true) Integer page,
                                                  @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        PageVo<GoodsVo> pageVo = miaoshaService.search(page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }

    @RequestMapping("/url")
    @ResponseBody
    @AccessLimit(needLogin = true)
    public ResultUtil<String> createUrl(User user, Integer goodsId) {
        String key = getMiaoshaUrl(user.getId(), goodsId);
        return ResultUtil.SUCCESS(key);
    }

    @RequestMapping("/{url}/do")
    @AccessLimit(needLogin = true, isMiaosha = true, canRepeat = false, limit = 3, expireTime = 5)
    public String doMiaosha(@PathVariable(value = "url") String url, User user, @NotNull Integer goodsId, @NotNull Integer code) {
        //---------------------------------
        //判断秒杀路径
        String localUrl = getMiaoshaUrl(user.getId(), goodsId);
        if (!localUrl.equals(url)) {
            return RedisService.beanToString(ResultUtil.ERROR(CodeMsg.URL_ERROR));
        }
        //---------------------------------

        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        GoodsVo goodsVo = miaoshaService.getByGoodsId(goodsId);
        message.setGoodsVo(goodsVo);
        miaoshaSender.sendMiaoshaMessage(message);
        return RedisService.beanToString(ResultUtil.SUCCESS(message));
    }

    //测试秒杀去除秒杀路径以及验证码
    @RequestMapping("/test/do")
    @AccessLimit(needLogin = true, isMiaosha = true, canRepeat = false, isTest = true)
    public String doMiaosha(User user, @NotNull Integer goodsId) {
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        GoodsVo goodsVo = miaoshaService.getByGoodsId(goodsId);
        message.setGoodsVo(goodsVo);
        miaoshaSender.sendMiaoshaMessage(message);
        return RedisService.beanToString(ResultUtil.SUCCESS(message));
    }

    private String getMiaoshaUrl(Integer userId, Integer goodsId) {
        String key = String.format(MIAOSHA_URL, userId, goodsId);
        String url = redisService.get(MiaoshaKeyPre.MIAOSHA_URL, key, String.class);
        if (url == null) {
            url = MD5Util.md5(key);
            redisService.set(MiaoshaKeyPre.MIAOSHA_URL, key, url);
        }
        return url;
    }

    @RequestMapping("/get")
    @AccessLimit(needLogin = true)
    public ResultUtil<GoodsVo> get(Integer goodsId) {
        GoodsVo vo = miaoshaService.getByGoodsId(goodsId);
        if (vo == null || (vo.getStartTime() == null || vo.getStartTime() == null)) {
            throw new GlobalException(CodeMsg.NOT_EXIST);
        }
        return ResultUtil.SUCCESS(vo);
    }

    //已优化
    @RequestMapping("/owner")
    @ResponseBody
    @AccessLimit(needLogin = true)
    public ResultUtil<Map<String, Object>> ownerGoodsList(User user,
                                                          @RequestParam(value = "page", required = true) Integer page,
                                                          @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        PageVo<GoodsVo> pageVo = miaoshaService.getByCreatorId(user.getId(), page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }


    //已优化
    @RequestMapping("/over")
    @ResponseBody
    @AccessLimit(needLogin = true)
    public ResultUtil<String> overMiaosha(User user, Integer goodsId) {
        miaoshaService.over(user.getId(), goodsId);
        return ResultUtil.SUCCESS("success");
    }


    @RequestMapping("/result")
    @AccessLimit(needLogin = true, isMiaosha = false)
    public ResultUtil<String> result(User user, Integer goodsId) {
        //秒杀结果      --0：秒杀到了，有相关记录   1：没秒杀到，库存不足    2：没秒杀到，特殊情况，非正常因素
        Integer res = miaoshaService.getResultByUserIdAndGoodsId(user.getId(), goodsId);
        if (res == 0) {
            return ResultUtil.SUCCESS("success");
        } else if (res == 1) {
            return ResultUtil.ERROR(CodeMsg.MIAOSHA_OVER);
        } else {
            return ResultUtil.ERROR(CodeMsg.MIAOSHA_ERROR);
        }
    }

    @RequestMapping("/test")
    @AccessLimit(needLogin = true, isMiaosha = false)
    public ResultUtil<String> test(HttpServletRequest request, HttpServletResponse response) {
        ExcuteCmdUtil.TestMiaosha();
        return ResultUtil.SUCCESS("success");
    }

    /**
     * 获取验证码
     *
     * @param response
     */
    @AccessLimit(needLogin = true, isMiaosha = false)
    @RequestMapping("/code")
    public void getVerificationCode(User user, HttpServletResponse response, @RequestParam(value = "goodsId") long goodsId) {
        //设置response,禁止图像缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        //响应类型为图片类型
        response.setContentType("image/jpeg");
        //产生bufferedImage
        BufferedImage bi = generateVerificationCode(user.getId(), goodsId);

        //将产生的image用imageio写入response中
        try {
            //获取输出流
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(bi, "JPEG", os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建验证码
     *
     * @return
     */
    private BufferedImage generateVerificationCode(Integer userId, long goodsId) {
        int width = 100;
        int height = 30;
        int pointCount = 50;

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        //先画出一个底层矩形框
        g.drawRect(0, 0, width, height);
        //填充底层矩形框
        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        //在矩形框中填充若干条干扰线
        g.setColor(Color.green);
        Random rd = new Random();
        for (int i = 0; i < pointCount; i++) {
            int x1 = rd.nextInt(width);
            int y1 = rd.nextInt(height);
            int x2 = rd.nextInt(width);
            int y2 = rd.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        //在里面写上数学公式
        String express = getCodeMain(rd);
        //使用scriptEngine进行数学表达式的解析以及计算
        ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            //获取表达式的值
            Object result = se.eval(express);
            //将该值存入redis中
            redisService.set(VerificationCodeKey.CODE_RESULT, String.format(CODE_RESULT_KEY, userId, goodsId), result);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        g.setColor(Color.black);
        Font oldFont = g.getFont();
        g.setFont(new Font(oldFont.getFontName(), oldFont.getStyle(), 28));
        g.drawString(express, 17, 25);
        g.dispose();
        return bi;
    }

    /**
     * 获取验证码中的数学公式
     */
    private String[] oprate = {"+", "-", "*"};

    private String getCodeMain(Random rd) {
        int e1 = rd.nextInt(10);
        int op1_index = rd.nextInt(oprate.length);
        int e2 = rd.nextInt(10);
        int op2_index = rd.nextInt(oprate.length);
        int e3 = rd.nextInt(10);
        return "" + e1 + oprate[op1_index] + e2 + oprate[op2_index] + e3;
    }
}
