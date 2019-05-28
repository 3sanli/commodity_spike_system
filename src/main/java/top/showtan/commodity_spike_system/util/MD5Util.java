package top.showtan.commodity_spike_system.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author: sanli
 * @Date: 2019/3/28 15:00
 */
public class MD5Util {
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }
}
