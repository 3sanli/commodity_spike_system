package top.showtan.commodity_spike_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.service.GoodsService;
import top.showtan.commodity_spike_system.service.MiaoshaService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.ModelAndViewUtil;
import top.showtan.commodity_spike_system.vo.GoodsVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: sanli
 * @Date: 2019/4/12 19:45
 */
@Controller
public class IndexController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    UserController userController;

    @RequestMapping({"/goods/home", "/"})
    @AccessLimit(needPermission = true, permission = 1)
    public ModelAndView goodsHome(User user) {
        ModelAndView modelAndView = ModelAndViewUtil.CreateModelAndView("goodsHome");
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    /**
     * 登录页面
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/login")
    public ModelAndView toLogin(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    /**
     * 登录页面
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/register")
    public ModelAndView toRegister(ModelAndView modelAndView) {
        modelAndView.setViewName("register");
        return modelAndView;
    }


    /**
     * 商家综合页面
     *
     * @param user
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/merchant")
    @AccessLimit(needLogin = true, needPermission = true, permission = {2, 3})
    public ModelAndView toMerchant(User user, ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        //此页面为商家专属页面，必须要先登录，若出现无登录用户则做跳转
        if (user == null) {
            modelAndView.setViewName("login");
            return modelAndView;
        }
        modelAndView.setViewName("merchant");
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    /**
     * 商家添加商品页面
     *
     * @param goodsId
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/goods/add")
    @AccessLimit(needLogin = true, needPermission = true, permission = 2)
    public ModelAndView toGoodsAdd(@RequestParam(value = "goodsId", required = false) Integer goodsId, ModelAndView modelAndView) {
        modelAndView.setViewName("goodsAdd");
        if (goodsId != null) {
            GoodsVo goodsVo = goodsService.getById(goodsId);
            modelAndView.addObject("goods", goodsVo);
        }
        return modelAndView;
    }

    /**
     * 展示商家所拥有所有商品页面
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/goods/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 2)
    public ModelAndView toListGoods(ModelAndView modelAndView) {
        modelAndView.setViewName("goodsList");
        return modelAndView;
    }

    /**
     * 所有分类信息页面
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/category/all")
    @AccessLimit(needLogin = true)
    public ModelAndView allCategory(ModelAndView modelAndView) {
        modelAndView.setViewName("allCategory");
        return modelAndView;
    }

    /**
     * 添加秒杀商品页面
     *
     * @param goodsId
     * @param modelAndView
     * @param response
     * @return
     */
    @RequestMapping("/portal/miaosha/add")
    @AccessLimit(needLogin = true, needPermission = true, permission = 2)
    public ModelAndView addMiaosha(@RequestParam(value = "goodsId", required = true) Integer goodsId, ModelAndView modelAndView, HttpServletResponse response) {
        modelAndView.setViewName("miaoshaAdd");
        //判断是否已存在秒杀活动
        if (goodsId != null) {
            doIfIsExist(goodsId, response);
        }
        modelAndView.addObject("goodsId", goodsId);
        return modelAndView;
    }

    private void doIfIsExist(Integer goodsId, HttpServletResponse response) {
        GoodsVo goodsVo = miaoshaService.getByGoodsId(goodsId);
        if (isMiaosha(goodsVo)) {
            throw new GlobalException(CodeMsg.MIAOSHA_GOODS_EXIST);
        }
    }

    private boolean isMiaosha(GoodsVo goodsVo) {
        return goodsVo != null && goodsVo.getStartTime() != null && goodsVo.getEndTime() != null;
    }


    /**
     * 修改秒杀商品属性
     *
     * @param goodsId
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/miaosha/modify")
    @AccessLimit(needLogin = true, needPermission = true, permission = 2)
    public ModelAndView modifyMiaosha(@RequestParam(value = "goodsId", required = true) Integer goodsId, ModelAndView modelAndView) {
        modelAndView.setViewName("miaoshaAdd");
        modelAndView.addObject("goodsId", goodsId);
        GoodsVo goodsVo = miaoshaService.getByGoodsId(goodsId);
        modelAndView.addObject("miaoshaGoods", goodsVo);
        return modelAndView;
    }

    /**
     * 展示商家所拥有秒杀商品页面
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/portal/miaosha/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 2)
    public ModelAndView listMiaosha(ModelAndView modelAndView) {
        modelAndView.setViewName("miaoshaList");
        return modelAndView;
    }

    /**
     * 普通商品展示页面
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/portal/simple/goods/detail/{goodsId}")
    @AccessLimit(needPermission = true, permission = 1)
    public ModelAndView simpleGoodsDetail(User user, @PathVariable(value = "goodsId") Integer goodsId) {
        ModelAndView modelAndView = ModelAndViewUtil.CreateModelAndView("detail");
        modelAndView.addObject("goodsId", goodsId);
        modelAndView.addObject("isMiaosha", false);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 秒杀商品展示页面
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/portal/miaosha/goods/detail/{goodsId}")
    @AccessLimit(needPermission = true, permission = 1)
    public ModelAndView miaoshaGoodsDetail(User user, @PathVariable(value = "goodsId") Integer goodsId) {
        ModelAndView modelAndView = ModelAndViewUtil.CreateModelAndView("detail");
        modelAndView.addObject("goodsId", goodsId);
        modelAndView.addObject("isMiaosha", true);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    /**
     * 订单页面
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/order/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 1)
    public ModelAndView listOrder(User user) {
        ModelAndView modelAndView = ModelAndViewUtil.CreateModelAndView("orderList");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 最新活动页面
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/miaosha/home")
    @AccessLimit(needPermission = true, permission = 1)
    public ModelAndView miaoshaHome(User user) {
        ModelAndView modelAndView = ModelAndViewUtil.CreateModelAndView("miaoshaHome");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 所有用户信息
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/user/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ModelAndView toUserList(User user, ModelAndView modelAndView) {
        modelAndView.setViewName("userList");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 所有订单信息
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/order/all/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ModelAndView toAllOrderList(User user, ModelAndView modelAndView) {
        modelAndView.setViewName("orderAllList");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 所有商品信息
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/goods/all/list")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ModelAndView toAllGoodsList(User user, ModelAndView modelAndView) {
        modelAndView.setViewName("goodsAllList");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    /**
     * 秒杀测试页面
     *
     * @param user
     * @return
     */
    @RequestMapping("/portal/miaosha/test")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ModelAndView toMiaoshaTest(User user, ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.setViewName("miaoshaTest");
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
