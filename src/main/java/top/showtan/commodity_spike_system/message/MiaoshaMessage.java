package top.showtan.commodity_spike_system.message;

import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.vo.GoodsVo;

/**
 * @Author: sanli
 * @Date: 2019/4/2 15:46
 */
public class MiaoshaMessage {
    private User user;
    private GoodsVo goodsVo;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
}
