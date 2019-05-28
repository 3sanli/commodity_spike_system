package top.showtan.commodity_spike_system.util;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:39
 */
public class CodeMsg {
    int code;
    String msg;

    public String getMsg() {
        return msg;
    }

    public CodeMsg() {
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //普通公共异常
    public static CodeMsg COMMON_EXCEPTION = new CodeMsg(100, "服务器出现异常");
    public static CodeMsg NEED_GOODS_ID = new CodeMsg(101, "缺少商品id");


    //登录模块
    public static CodeMsg MOBILE_NOT_NULL = new CodeMsg(201, "号码不为空");
    public static CodeMsg PASSWORD_NOT_NULL = new CodeMsg(202, "密码不为空");
    public static CodeMsg USER_NOT_EXISTS = new CodeMsg(203, "用户不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(204, "密码不正确");
    public static CodeMsg NEED_LOGIN = new CodeMsg(205, "请登录");


    //商品模块
    public static CodeMsg GOODS_NOT_EXISTS = new CodeMsg(301, "不存在该商品");
    public static CodeMsg EXIST_MIAOSHA = new CodeMsg(302, "存在秒杀活动");
    public static CodeMsg EXIST_ORDER = new CodeMsg(303, "存在订单");


    //购买模块
    public static CodeMsg GOODS_STOCK_EMPTY = new CodeMsg(401, "商品库存不足");

    //秒杀模块
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(501, "秒杀活动结束");
    public static CodeMsg MIAOSHA_REPEAT = new CodeMsg(502, "重复秒杀");
    public static CodeMsg MIAOSHA_ERROR = new CodeMsg(503, "秒杀失败");
    public static CodeMsg MIAOSHA_GOODS_EXIST = new CodeMsg(504, "秒杀活动已存在");
    public static CodeMsg CODE_LIMIT = new CodeMsg(505, "验证码错误");
    public static CodeMsg NOT_EXIST = new CodeMsg(506, "秒杀活动不存在");
    public static CodeMsg NEED_CODE = new CodeMsg(507, "缺少验证码");
    public static CodeMsg URL_ERROR = new CodeMsg(508, "秒杀路径不正确");
    public static CodeMsg MIAOSHA_LIMIT = new CodeMsg(509, "秒杀请求过多，请稍后再操作");




    //注册模块
    public static CodeMsg USER_EXIST = new CodeMsg(601, "秒杀活动结束");


    public CodeMsg fillError(Object... args) {
        int code = COMMON_EXCEPTION.code;
        String msg = String.format("%s", args);
        return new CodeMsg(code, msg);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
