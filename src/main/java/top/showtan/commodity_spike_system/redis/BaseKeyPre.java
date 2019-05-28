package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/3/25 13:42
 */
public class BaseKeyPre {
    int expire;
    String keyPre;

    BaseKeyPre(){}
    BaseKeyPre(int expire,String keyPre){
        this.expire = expire;
        this.keyPre = keyPre;
    }

    public int getExpire() {
        return expire;
    }

    public String getKeyPre() {
        return keyPre;
    }
}
