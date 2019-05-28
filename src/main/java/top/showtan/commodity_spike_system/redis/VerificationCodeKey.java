package top.showtan.commodity_spike_system.redis;

/**
 * @Author: sanli
 * @Date: 2019/4/25 21:42
 */
public class VerificationCodeKey extends BaseKeyPre{
    public VerificationCodeKey(int expire, String keyPre ){
        super(expire,keyPre);
    }
    public static final BaseKeyPre CODE_RESULT = new VerificationCodeKey(3600, "CODE_RESULT");


}
