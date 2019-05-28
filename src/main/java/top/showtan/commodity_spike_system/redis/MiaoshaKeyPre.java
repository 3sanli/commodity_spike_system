package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/4/2 16:28
 */
public class MiaoshaKeyPre extends BaseKeyPre {
    public MiaoshaKeyPre(int expire, String keyPre) {
        super(expire, keyPre);
    }

    public static MiaoshaKeyPre MIAOSHA_STOCK = new MiaoshaKeyPre(36000, "MIAOSHA_STOCK");
    public static MiaoshaKeyPre MIAOSHA_REPEAT = new MiaoshaKeyPre(36000, "MIAOSHA_REPEAT");
    public static MiaoshaKeyPre OWNER_MIAOSHA_GOODS = new MiaoshaKeyPre(36000, "OWNER_MIAOSHA_GOODS");
    public static MiaoshaKeyPre MIAOSHA_URL = new MiaoshaKeyPre(36000, "MIAOSHA_URL");

    public static MiaoshaKeyPre LIMIT_TIME(int expireTime) {
        return new MiaoshaKeyPre(expireTime, "LIMIT_TIME");
    }

}
