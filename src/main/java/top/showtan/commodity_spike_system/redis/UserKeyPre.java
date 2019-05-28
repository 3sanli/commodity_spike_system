package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/3/25 14:21
 */
public class UserKeyPre extends BaseKeyPre {
    private UserKeyPre(int expire, String keyPre) {
        super(expire, keyPre);
    }

    public static UserKeyPre USER_LOGIN() {
        return new UserKeyPre(560000, "USER_LOGIN");
    }
}
