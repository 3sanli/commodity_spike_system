package top.showtan.commodity_spike_system.entity;

/**
 * @Author: sanli
 * @Date: 2019/3/26 20:21
 */
public class GoodsCategory {
    private Integer id;
    private Integer goodsId;
    private Integer categoryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
