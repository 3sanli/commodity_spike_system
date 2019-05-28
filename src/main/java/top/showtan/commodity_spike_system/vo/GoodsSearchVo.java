package top.showtan.commodity_spike_system.vo;

/**
 * @Author: sanli
 * @Date: 2019/3/28 15:35
 */
public class GoodsSearchVo {
    private String goodsName;
    private Integer categoryId;
    private Integer goodsId;
    private Integer model;          //--1:普通商品     2:秒杀商品

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

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
