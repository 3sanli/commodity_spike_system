package top.showtan.commodity_spike_system.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import top.showtan.commodity_spike_system.entity.User;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Author: sanli
 * @Date: 2019/4/9 14:52
 */
public class UserUtil {
    private static String[] mobilePre = {"188", "181", "183", "187", "136", "137", "138", "147", "150", "151", "157", "158", "159"};
    private static String commonPassword = "123456";
    private static int userCount = 1000;
    public static String localAddUserUrl = "http://localhost:8080/user/addWithId";
    public static String localLoginUserUrl = "http://localhost:8080/user/login";

    public static String remoteAddUserUrl = "http://localhost:8087/user/addWithId";
    public static String remoteLoginUserUrl = "http://localhost:8087/user/login";

    public static String localUserInfoPath = "D:\\userInfo.txt";
    public static String remoteUserInfoPath = "/root/miaoshaTest/userInfo.txt";

    //TODO 远程和本地要改
    private static UserUtilBean bean = UserUtilBean.remoteUserUtilBean;

    public static void main(String[] args) throws IOException {
        createUsers();
    }


    public static void createUsers() {
        //创建用户，电话、昵称以及密码
        File file = new File(bean.getUserInfoPath());
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            int startId = 3;
            for (int i = 0; i < userCount; i++) {
                User user = createUser(++startId);
                addUser(user,bean.getAddUserUrl());
                String token = loginUser(user,bean.getLoginUserUrl());
                bw.write(user.getId() + "," + token);
                bw.flush();
                if (i != userCount - 1) {
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loginUser(User user,String loginUserUrl) {
        String token = new String();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(loginUserUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("mobile", user.getMobile()));
        params.add(new BasicNameValuePair("password", user.getPassword()));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String tokenJson = EntityUtils.toString(responseEntity);
                ResultUtil responseResult = JSON.parseObject(tokenJson, ResultUtil.class);
                token = (String) ((Map) responseResult.getData()).get("token");
            }
            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    private static void addUser(User user,String addUserUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(addUserUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", "" + user.getId()));
        params.add(new BasicNameValuePair("mobile", user.getMobile()));
        params.add(new BasicNameValuePair("nickName", user.getNickName()));
        params.add(new BasicNameValuePair("password", user.getPassword()));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
            post.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            response.close();
            if (responseEntity != null) {
                System.out.println(responseEntity.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static User createUser(int id) {
        Random random = new Random();
        int preIndex = random.nextInt(13);
        int midNum = random.nextInt(4444) + 1111;
        int rightNum = random.nextInt(5555) + 4444;
        String mobile = mobilePre[preIndex] + midNum + rightNum;
        return new User(id, mobile, mobile, commonPassword);
    }

    public static void loginAll() {
        List<User> users = getAllUserFromDB();
        File file = new File(bean.getUserInfoPath());
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            int i = 0;
            for (User user : users) {
                i++;
                String token = loginUser(user,bean.getLoginUserUrl());
                bw.write(user.getId() + "," + token);
                bw.flush();
                if (i != users.size()) {
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<User> getAllUserFromDB() {
        Connection conn = JDBCUtil.getConn();
        String sql = "select user_id,mobile,password from user where user_id!=1 and user_id!=2 and user_id!=3";
        List<User> users = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery(sql);
            while (set.next()) {
                User user = new User();
                int id = set.getInt("user_id");
                String mobile = set.getString("mobile");
                String password = set.getString("password");
                user.setId(id);
                user.setMobile(mobile);
                user.setPassword(password);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
