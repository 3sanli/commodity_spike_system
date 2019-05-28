package top.showtan.commodity_spike_system.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.showtan.commodity_spike_system.controller.UserController;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.redis.MiaoshaKeyPre;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.redis.UserKeyPre;
import top.showtan.commodity_spike_system.redis.VerificationCodeKey;
import top.showtan.commodity_spike_system.service.MiaoshaService;
import top.showtan.commodity_spike_system.service.UserService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.Constant;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.controller.MiaoshaController;
import top.showtan.commodity_spike_system.vo.OrderVo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: sanli
 * @Date: 2019/4/2 17:02
 */
@Component
public class MyHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    RedisService redisService;

    @Autowired
    UserService userService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    UserController userController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            AccessLimit limit = ((HandlerMethod) handler).getMethodAnnotation(AccessLimit.class);
            //根据permission初始化用户数据(以及将用户信息放入线程本地中)
            initUserContent(limit, request, response);
            //需要登录的判断
            if (limit != null && doNeedLogin(limit)) {
                if (!isLogin(request, response)) {
                    failureLogin(response);
                    return false;
                }
                //是否为秒杀活动判断 && 重复秒杀处理
                if (isMiaosha(limit)) {
                    //判断是否为测试环境
                    if (limit.isTest()) {
                        return doIfTest(limit, request, response);
                    } else {
                        return doIfNotTest(limit, request, response);
                    }
                }
            }
        }
        return true;
    }

    private boolean doIfNotTest(AccessLimit limit, HttpServletRequest request, HttpServletResponse response) {
        //先判断限流条件
        if (!limitPass(limit, request, response)) {
            return false;
        }
        if (repeat(limit, request, response)) {
            //秒杀库存是否充足
            boolean isRemain = miaoshaStock(request, response) == true ? true : false;
            //判断验证码
            return isRemain && validateCode(request, response);
        }
        return false;
    }

    private boolean doIfTest(AccessLimit limit, HttpServletRequest request, HttpServletResponse response) {
        if (repeat(limit, request, response)) {
            //秒杀库存是否充足
            boolean isRemain = miaoshaStock(request, response) == true ? true : false;
            return isRemain;
        }
        return false;
    }

    private void initUserContent(AccessLimit limit, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getCurr(request, response);
        if (limit != null && limit.needPermission()) {
            int[] permission = limit.permission();

            //判断权限，防止用户信息串了
            if (user != null && !allowPermission(user.getPermission(), permission)) {
                userController.deleteUserInfo(request, response);
            }
        }
    }

    private boolean allowPermission(Integer permission, int[] permissionArray) {
        if (permissionArray != null && permissionArray.length != 0) {
            for (int i : permissionArray) {
                if (i == permission) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean limitPass(AccessLimit limit, HttpServletRequest request, HttpServletResponse response) {
        if (!limit.isLimit()) {
            return true;
        }
        User currUser = UserContext.getUser();
        Integer goodsId = getUrlParam(request, "goodsId", Integer.class);
        if (goodsId == null) {
            ResultUtil res = ResultUtil.ERROR(CodeMsg.NEED_GOODS_ID);
            doResponse(response, res);
            return false;
        }
        String key = String.format(MiaoshaController.MIAOSHA_LIMIT, currUser.getId(), goodsId);
        int expireTime = limit.expireTime();
        int count = limit.limit();
        Integer limitTime = redisService.get(MiaoshaKeyPre.LIMIT_TIME(expireTime), key, Integer.class);
        if (limitTime == null) {
            redisService.set(MiaoshaKeyPre.LIMIT_TIME(expireTime), key, count);
            redisService.decr(MiaoshaKeyPre.LIMIT_TIME(expireTime), key);
            return true;
        }
        if (limitTime <= 0) {
            ResultUtil res = ResultUtil.ERROR(CodeMsg.MIAOSHA_LIMIT);
            doResponse(response, res);
            return false;
        }
        redisService.decr(MiaoshaKeyPre.LIMIT_TIME(expireTime), key);
        return true;
    }

    private boolean isLogout(HttpServletRequest request) {
        User user = UserContext.getUser();
        User cacheUser = redisService.get(UserKeyPre.USER_LOGIN(), getToken(request), User.class);
        if (cacheUser == null) {
            UserContext.logout();
        }
        return cacheUser == null;
    }

    private boolean validateCode(HttpServletRequest request, HttpServletResponse response) {
        Integer goodsId = getUrlParam(request, "goodsId", Integer.class);
        User currUser = UserContext.getUser();
        if (goodsId == null) {
            doResponse(response, ResultUtil.ERROR(CodeMsg.NEED_GOODS_ID));
            return false;
        }
        Integer codeView = getUrlParam(request, "code", Integer.class);
        if (codeView == null) {
            doResponse(response, ResultUtil.ERROR(CodeMsg.NEED_CODE));
            return false;
        }
        Integer code = redisService.get(VerificationCodeKey.CODE_RESULT, String.format(MiaoshaController.CODE_RESULT_KEY, currUser.getId(),goodsId), Integer.class);
        if (code == null) {
            doResponse(response, ResultUtil.ERROR(CodeMsg.NEED_CODE));
            return false;
        }
        boolean pass = codeView == code;
        if (!pass) {
            ResultUtil res = ResultUtil.ERROR(CodeMsg.CODE_LIMIT);
            doResponse(response, res);
        }
        return pass;
    }

    private <T> T getUrlParam(HttpServletRequest request, String paramName, Class<T> clazz) {
        String value = request.getParameter(paramName);
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            if (clazz == int.class) {
                return clazz.cast((Integer.valueOf(value)).intValue());
            } else if (clazz == Integer.class) {
                return clazz.cast(Integer.valueOf(value));
            } else {
                return clazz.cast(value);
            }
        }
    }


    private void doResponse(HttpServletResponse response, ResultUtil result) {
        try {
            ServletOutputStream os = response.getOutputStream();
            byte[] bytes = JSON.toJSONBytes(result);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean miaoshaStock(HttpServletRequest request, HttpServletResponse response) {
        Integer goodsId = Integer.valueOf(request.getParameter("goodsId"));
        boolean isRemain = miaoshaGoodsRemain(goodsId);
        if (!isRemain) {
            try {
                ServletOutputStream os = response.getOutputStream();
                ResultUtil result = ResultUtil.ERROR(CodeMsg.MIAOSHA_OVER);
                String msg = JSON.toJSONString(result);
                os.write(msg.getBytes());
                os.flush();
                os.close();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean miaoshaGoodsRemain(Integer goodsId) {
        //秒杀前判断不需要初始化
        if (StringUtils.isEmpty("" + goodsId)) {
            return false;
        }
        Integer stock = redisService.get(MiaoshaKeyPre.MIAOSHA_STOCK, "" + goodsId, Integer.class);
        if (stock == null) {
            stock = miaoshaService.getStockByGoodsId(goodsId);
            if (stock == null || stock <= 0) {
                return false;
            }
        }
        return stock > 0;
    }

    private boolean repeat(AccessLimit limit, HttpServletRequest request, HttpServletResponse response) {
        boolean canRepeat = limit.canRepeat();
        User currUser = UserContext.getUser();
        //不能重复秒杀
        if (!canRepeat) {
            Integer goodsId = Integer.valueOf(request.getParameter("goodsId"));
            //在redis中判断是否有相关秒杀记录
            boolean repeat = isRepeat(currUser.getId(), goodsId);
            if (repeat) {
                doResponse(response, ResultUtil.ERROR(CodeMsg.MIAOSHA_REPEAT));
                return false;
            }
        }
        //能重复秒杀以及还没有秒杀
        return true;
    }

    //controller中的判断
    public boolean isRepeat(Integer userId, Integer goodsId) {
        OrderVo orderVo = redisService.get(MiaoshaKeyPre.MIAOSHA_REPEAT, "" + userId + "_" + goodsId, OrderVo.class);
        if (orderVo == null) {
            orderVo = miaoshaService.getByUserIdAndGoodsId(userId, goodsId);
            if (orderVo == null) {
                return false;
            }
        }
        return true;
    }

    private boolean isMiaosha(AccessLimit limit) {
        if (limit.isMiaosha()) {
            return true;
        }
        return false;
    }

    public static String getToken(HttpServletRequest request) {
        //在这里会有一个显示bug，如果说登录的时候两个用户的话，那么cookies会出现覆盖token的情况
        String requestToken = request.getParameter("token");
        String cookiesToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.USER_KEY.equals(cookie.getName())) {
                    cookiesToken = cookie.getValue();
                }
            }
        }
        return requestToken == null ? cookiesToken == null ? null : cookiesToken : requestToken;
    }

    private boolean doNeedLogin(AccessLimit limit) {
        if (!limit.needLogin()) {
            return false;
        }
        return true;
    }

    private boolean isLogin(HttpServletRequest request, HttpServletResponse response) {
        //判断是否登录走cookies
        String token = getToken(request);
        if (token == null) {
            return false;
        }
        User user = userService.getByToken(token);
        userService.addToken(user, token, response);
        return true;
    }

    private void failureLogin(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ServletOutputStream outputStream = response.getOutputStream();
        ResultUtil error = ResultUtil.ERROR(CodeMsg.NEED_LOGIN);
        String msg = JSON.toJSONString(error);
        outputStream.write(msg.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
