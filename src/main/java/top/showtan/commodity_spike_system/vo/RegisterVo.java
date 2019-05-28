package top.showtan.commodity_spike_system.vo;

import top.showtan.commodity_spike_system.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * @Author: sanli
 * @Date: 2019/3/19 23:02
 */
public class RegisterVo {
    private Integer id;
    @NotNull
    @IsMobile
    private String mobile;

    private String nickName;

    @NotNull
    private String password;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
