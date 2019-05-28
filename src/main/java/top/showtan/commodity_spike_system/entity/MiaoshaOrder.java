package top.showtan.commodity_spike_system.entity;

/**
 * @Author: sanli
 * @Date: 2019/4/2 16:45
 */
public class MiaoshaOrder {
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private Integer orderId;

    public MiaoshaOrder() {
    }

    public MiaoshaOrder(Integer userId, Integer goodsId, Integer orderId) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
