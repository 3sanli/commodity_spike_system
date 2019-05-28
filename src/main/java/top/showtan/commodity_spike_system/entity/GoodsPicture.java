package top.showtan.commodity_spike_system.entity;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:11
 */
public class GoodsPicture {
    private Integer id;
    private Integer goodsId;
    private Integer pictureId;

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

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }
}
