package top.showtan.commodity_spike_system.vo;

import top.showtan.commodity_spike_system.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:02
 */
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
