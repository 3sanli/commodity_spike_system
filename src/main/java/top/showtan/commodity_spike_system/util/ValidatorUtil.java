package top.showtan.commodity_spike_system.util;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:23
 */
public class ValidatorUtil {
    //手机号验证
    public static boolean isMobile(String mobile) {
        //号码位数是否为11以及是否为1开头
        if (mobile.length() != 11 || !mobile.startsWith("1")) {
            return false;
        }
        //判断号码内是否有英文字符
        for (char c : mobile.toCharArray()) {
            if (c < '0' && c > '9') {
                return false;
            }
        }
        return true;
    }
}
