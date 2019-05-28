package top.showtan.commodity_spike_system.config;

import top.showtan.commodity_spike_system.entity.User;

/**
 * @Author: sanli
 * @Date: 2019/4/2 23:40
 */
public class UserContext {
    private static ThreadLocal<User> userContext = new ThreadLocal<>();

    public static User getUser() {
        return userContext.get();
    }

    public static void setUser(User user) {
        userContext.set(user);
    }

    public static void logout() {
        userContext.remove();
    }
}
