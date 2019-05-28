package top.showtan.commodity_spike_system.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * @Author: sanli
 * @Date: 2019/5/10 10:31
 */
public class ExcuteCmdUtil {
    //TODO 远程和本地不一样
    public static String cmdLocal = "cmd /c jmeter -n -t D:\\localSpike.jmx";
    public static String cmdRemote = "cd /root/JMETER/apache-jmeter-5.0/bin; ./jmeter -n -t /root/miaoshaTest/remoteSpike.jmx";

    public static void main(String[] args) {
        TestMiaosha();
    }

    public static void TestMiaosha() {
        //使用linux
        //TODO 记得修改ip
        doTestMiaosha(ConnectBean.remoteConnectBean);
    }

    public static void doTestMiaosha(ConnectBean connectBean) {
        try {
            //建立连接
            Connection conn = new Connection(connectBean.getHostname());
            conn.connect();
            //利用用户名和密码进行授权
            boolean isAuthenticated = conn.authenticateWithPassword(connectBean.getUsername(), connectBean.getPassword());
            if (isAuthenticated == false) {
                throw new IOException("Authorication failed");
            }
            //打开会话
            Session sess = conn.openSession();
            //执行命令
            sess.execCommand(connectBean.getCmd());
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                System.out.println(line);
            }
            sess.close();
            conn.close();

        } catch (IOException e) {
            System.out.println("can not access the remote machine");
        }
    }
}
