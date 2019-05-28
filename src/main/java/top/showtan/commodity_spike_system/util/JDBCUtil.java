package top.showtan.commodity_spike_system.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author: sanli
 * @Date: 2019/4/12 13:48
 */
public class JDBCUtil {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        Connection connection = null;
        try {
            String url = "jdbc:mysql://47.106.244.103:3306/commodity_spike_system?useUnicode=true&characterEncoding=UTF-8";
            String user = "root";
            String password = "LXD$donggua123";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
