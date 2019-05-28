package top.showtan.commodity_spike_system.datasource;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @Author: sanli
 * @Date: 2019/4/9 14:31
 */
@WebServlet(urlPatterns = "/druid/*",
        initParams = {
                @WebInitParam(name = "loginUsername", value = "admin"),
                @WebInitParam(name = "loginPassword", value = "123456"),
                @WebInitParam(name = "resetEnable", value = "false")
        }
)
public class DruidStatViewServlet extends StatViewServlet {

}
