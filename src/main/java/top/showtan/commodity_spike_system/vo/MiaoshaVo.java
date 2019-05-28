package top.showtan.commodity_spike_system.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: sanli
 * @Date: 2019/3/26 18:42
 */
public class MiaoshaVo {
    private Integer id;
    @NotNull
    private Integer goodsId;
    @Min(value = 0)
    private Integer miaoshaStock;
    @Min(value = 0)
    private Long miaoshaPrice;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;

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

    public Integer getMiaoshaStock() {
        return miaoshaStock;
    }

    public void setMiaoshaStock(Integer miaoshaStock) {
        this.miaoshaStock = miaoshaStock;
    }

    public Long getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Long miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
