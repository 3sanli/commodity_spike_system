package top.showtan.commodity_spike_system.vo;

import top.showtan.commodity_spike_system.entity.OrderInfo;


/**
 * @Author: sanli
 * @Date: 2019/4/1 19:56
 */
public class OrderVo extends OrderInfo {
    //是否为秒杀订单
    private boolean isMiaosha;
    private Integer miaoshaOrderId;

    public boolean isMiaosha() {
        return isMiaosha;
    }

    public void setMiaosha(boolean miaosha) {
        isMiaosha = miaosha;
    }

    public Integer getMiaoshaOrderId() {
        return miaoshaOrderId;
    }

    public void setMiaoshaOrderId(Integer miaoshaOrderId) {
        this.miaoshaOrderId = miaoshaOrderId;
    }
}
