package top.showtan.commodity_spike_system.util;


/**
 * @Author: sanli
 * @Date: 2019/5/15 16:55
 */
public class UserUtilBean {
    private String addUserUrl;
    private String loginUserUrl;
    private String userInfoPath;


    public UserUtilBean(String addUserUrl, String loginUserUrl, String userInfoPath) {
        this.addUserUrl = addUserUrl;
        this.loginUserUrl = loginUserUrl;
        this.userInfoPath = userInfoPath;
    }

    public static UserUtilBean localUserUtilBean = new UserUtilBean(UserUtil.localAddUserUrl, UserUtil.localLoginUserUrl, UserUtil.localUserInfoPath);
    public static UserUtilBean remoteUserUtilBean = new UserUtilBean(UserUtil.remoteAddUserUrl, UserUtil.remoteLoginUserUrl, UserUtil.remoteUserInfoPath);

    public String getAddUserUrl() {
        return addUserUrl;
    }

    public void setAddUserUrl(String addUserUrl) {
        this.addUserUrl = addUserUrl;
    }

    public String getLoginUserUrl() {
        return loginUserUrl;
    }

    public void setLoginUserUrl(String loginUserUrl) {
        this.loginUserUrl = loginUserUrl;
    }

    public String getUserInfoPath() {
        return userInfoPath;
    }

    public void setUserInfoPath(String userInfoPath) {
        this.userInfoPath = userInfoPath;
    }
}
