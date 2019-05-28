package top.showtan.commodity_spike_system.vo;

/**
 * @Author: sanli
 * @Date: 2019/5/10 14:49
 */
public class OrderSearchVo {
    private Integer goodsId;
    private Integer model;          //支持普通订单与秒杀订单的切换  --1:普通订单，2:秒杀订单


    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }
}
