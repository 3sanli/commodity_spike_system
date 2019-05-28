package top.showtan.commodity_spike_system.util;

/**
 * @Author: sanli
 * @Date: 2019/5/12 13:55
 */
public class ConnectBean {
    private String hostname;
    private String username;
    private String password;
    private String cmd;

    public ConnectBean(String hostname, String username, String password, String cmd) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.cmd = cmd;
    }

    public static ConnectBean localConnectBean = new ConnectBean("localhost", "admin", "123", ExcuteCmdUtil.cmdLocal);
    public static ConnectBean remoteConnectBean = new ConnectBean("47.106.244.103", "root", "LXDdonggua123", ExcuteCmdUtil.cmdRemote);


    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
