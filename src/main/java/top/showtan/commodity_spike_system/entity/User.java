package top.showtan.commodity_spike_system.entity;

/**
 * @Author: sanli
 * @Date: 2019/3/19 22:30
 */
public class User {
    private Integer id;
    private String mobile;
    private String nickName;
    private String password;
    private Integer iconId;
    private Integer permission;

    public User() {
    }

    public User(Integer id, String mobile, String nickName, String password) {
        this.id = id;
        this.mobile = mobile;
        this.nickName = nickName;
        this.password = password;
    }

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

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "User{" +
                "mobile='" + mobile + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
