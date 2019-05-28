package top.showtan.commodity_spike_system.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.showtan.commodity_spike_system.config.MyHandlerInterceptor;
import top.showtan.commodity_spike_system.config.UserContext;
import top.showtan.commodity_spike_system.dao.GoodsMapper;
import top.showtan.commodity_spike_system.dao.UserMapper;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.exception.GlobalException;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.redis.UserKeyPre;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.Constant;
import top.showtan.commodity_spike_system.vo.PageVo;
import top.showtan.commodity_spike_system.vo.UserVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @Author: sanli
 * @Date: 2019/3/19 23:35
 */
@Transactional
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisService redisService;

    public String doLogin(User userVo, HttpServletRequest request, HttpServletResponse response) {
        //判断mobile和password是否为空
        if (StringUtils.isEmpty(userVo.getMobile())) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_NULL);
        } else if (StringUtils.isEmpty(userVo.getPassword())) {
            throw new GlobalException(CodeMsg.PASSWORD_NOT_NULL);
        }
        //用loginVo中的mobile属性查出数据库中对应登录密码
        User user = userMapper.selectByMobie(userVo.getMobile());
        //不存在该用户
        if (user == null) {
            throw new GlobalException(CodeMsg.USER_NOT_EXISTS);
        }
        //密码不正确
        if (!userVo.getPassword().equals(user.getPassword())) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = MyHandlerInterceptor.getToken(request);
        if (StringUtils.isEmpty(token)) {
            token = UUID.randomUUID().toString();
        }
        addToken(user, token, response);
        BeanUtils.copyProperties(user, userVo);
        return token;
    }

    public User getByToken(String token) {
        return redisService.get(UserKeyPre.USER_LOGIN(), token, User.class);
    }

    public void addToken(User user, String token, HttpServletResponse response) {
        UserKeyPre keyPre = UserKeyPre.USER_LOGIN();
        Cookie cookie = new Cookie(Constant.USER_KEY, token);
        cookie.setPath("/");
        cookie.setMaxAge(keyPre.getExpire());
        response.addCookie(cookie);
        redisService.set(keyPre, token, user);
        UserContext.setUser(user);
    }

    public User getById(Integer userId) {
        if (userId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new GlobalException(CodeMsg.USER_NOT_EXISTS);
        }
        return user;
    }

    public User modify(User user, HttpServletRequest request) {
        //修改之前将缓存中的数据删除，修改之后加入缓存
        String token = MyHandlerInterceptor.getToken(request);
        redisService.delete(UserKeyPre.USER_LOGIN(), token);
        //修改用户信息
        userMapper.modify(user);
        user = userMapper.selectById(user.getId());
        //将用户最新信息加入缓存
        redisService.set(UserKeyPre.USER_LOGIN(), token, user);
        return user;
    }

    public boolean isExist(String mobile) {
        User user = userMapper.selectByMobie(mobile);
        return user != null;
    }

    public void doAddWithId(User user) {
        userMapper.addWithId(user);
    }

    public void doAddWithOutId(User user) {
        userMapper.addWithOutId(user);
    }

    public List<User> getAll() {
        List<User> userList = userMapper.getAll();
        if (userList == null) {
            return new ArrayList<>();
        }
        return userList;
    }

    public void cleanAllForTest() {
        userMapper.cleanAllForTest();
    }

    public PageVo<UserVo> search(Integer page, Integer pageCount) {
        PageVo<UserVo> pageVo = new PageVo<>();
        Long totalCounts = getCounts();
        pageVo.setDataCounts(totalCounts);
        if (totalCounts == 0) {
            pageVo.setData(new ArrayList<>());
            return pageVo;
        }
        pageVo.createPageSearchConstant(page, pageCount);
        List<UserVo> userVos = userMapper.search(pageVo.getOffset(), pageVo.getTake());

        pageVo.setData(userVos);
        if (userVos == null) {
            pageVo.setData(new ArrayList<>());
        }

        return pageVo;
    }

    private Long getCounts() {
        return userMapper.getCounts();
    }

    public void deleteById(Integer userId) {
        if (userId == null) {
            throw new GlobalException(CodeMsg.COMMON_EXCEPTION);
        }
        userMapper.deleteById(userId);
    }

    public User getCurr(HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        String token = MyHandlerInterceptor.getToken(request);
        if (!StringUtils.isEmpty(token)) {
            user = getByToken(token);
            addToken(user, token, response);
        } else {
            //若token为空即用户登出
            UserContext.logout();
        }
        return user;
    }
}
