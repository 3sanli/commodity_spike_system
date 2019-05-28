package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/4/1 15:58
 */
public class CategoryKeyPre extends BaseKeyPre {
    public CategoryKeyPre(int expire, String keyPre) {
        super(expire, keyPre);
    }

    public static CategoryKeyPre IS_PARENT = new CategoryKeyPre(36000, "IS_PARENT");
}
