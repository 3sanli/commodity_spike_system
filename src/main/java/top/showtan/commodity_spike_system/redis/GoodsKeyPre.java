package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/3/28 14:56
 */
public class GoodsKeyPre extends BaseKeyPre {
    public GoodsKeyPre(int expire, String keyPre) {
        super(expire, keyPre);
    }

    public static GoodsKeyPre GOODS_INFO = new GoodsKeyPre(3000, "GOODS_INFO");
    public static GoodsKeyPre GOODS_STOCK = new GoodsKeyPre(36000, "GOODS_STOCK");
    public static GoodsKeyPre OWNER_SIMAPLE_GOODS = new GoodsKeyPre(36000, "OWNER_SIMAPLE_GOODS");

}
