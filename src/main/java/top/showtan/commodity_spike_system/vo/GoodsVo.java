package top.showtan.commodity_spike_system.vo;


import org.springframework.format.annotation.DateTimeFormat;
import top.showtan.commodity_spike_system.entity.Goods;
import top.showtan.commodity_spike_system.entity.Picture;
import top.showtan.commodity_spike_system.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:51
 */
public class GoodsVo extends Goods {
    private Integer miaoshaStock;
    private Long miaoshaPrice;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Integer categoryId;
    private User user;
    private Picture goodsPicture;

    private boolean isOver;
    //秒杀活动状态   --  -1：秒杀已结束    0：秒杀未开始   1：秒杀进行中
    private Integer status;
    private Long remainTime;

    private boolean isMiaosha;


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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Picture getGoodsPicture() {
        return goodsPicture;
    }

    public void setGoodsPicture(Picture goodsPicture) {
        this.goodsPicture = goodsPicture;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Long remainTime) {
        this.remainTime = remainTime;
    }

    public String getRemainTimeView() {
        if (getRemainTime() == null) {
            return "";
        }
        Long seconds = getRemainTime() / 1000;
        int hours = (int) (seconds / 3600);
        int min = (int) ((seconds - hours * 3600 + 60) / 60);
        String hoursRes = "" + hours + "小时";
        String minRes = "" + min + "分钟";
        String result = hours == 0 ? minRes : hoursRes + "," + minRes;
        return result;
    }

    public boolean isMiaosha() {
        return isMiaosha;
    }

    public void setMiaosha(boolean miaosha) {
        isMiaosha = miaosha;
    }
}
