package top.showtan.commodity_spike_system.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.showtan.commodity_spike_system.config.AccessLimit;
import top.showtan.commodity_spike_system.config.UserContext;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.redis.RedisService;
import top.showtan.commodity_spike_system.redis.UserKeyPre;
import top.showtan.commodity_spike_system.service.UserService;
import top.showtan.commodity_spike_system.util.CodeMsg;
import top.showtan.commodity_spike_system.util.Constant;
import top.showtan.commodity_spike_system.util.ResultUtil;
import top.showtan.commodity_spike_system.util.UserUtil;
import top.showtan.commodity_spike_system.vo.LoginVo;
import top.showtan.commodity_spike_system.vo.PageVo;
import top.showtan.commodity_spike_system.vo.RegisterVo;
import top.showtan.commodity_spike_system.vo.UserVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: sanli
 * @Date: 2019/3/19 22:37
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/curr")
    @AccessLimit(needLogin = true)
    public ResultUtil<User> curr(User user) {
        return ResultUtil.SUCCESS(user);
    }

    @RequestMapping("/login")
    public ResultUtil<Map<String, Object>> login(@Validated LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        BeanUtils.copyProperties(loginVo, user);
        String token = userService.doLogin(user, request, response);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("user", user);
        return ResultUtil.SUCCESS(resultMap);
    }

    @RequestMapping("/logout")
    @AccessLimit(needLogin = true)
    public ResultUtil<String> logout(HttpServletRequest request, HttpServletResponse response) {
        deleteUserInfo(request, response);
        return ResultUtil.SUCCESS("success");
    }

    public void deleteUserInfo(HttpServletRequest request, HttpServletResponse response) {
        UserContext.logout();
        //删除cookies中的登录信息
        Cookie[] cookies = request.getCookies();
        String token = "";
        if (null == cookies) {
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constant.USER_KEY)) {
                    token = cookie.getValue();
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        //删除redis中的登录信息
        if (!StringUtils.isEmpty(token)) {
            redisService.delete(UserKeyPre.USER_LOGIN(), token);
        }
    }

    @RequestMapping("/get")
    @AccessLimit(needLogin = true)
    public ResultUtil<User> get(User user, @NotNull Integer userId) {
        User userInfo = userService.getById(userId);
        return ResultUtil.SUCCESS(userInfo);
    }

    @RequestMapping("/modify")
    @AccessLimit(needLogin = true)
    public ResultUtil<User> modify(User user, UserVo userVo, HttpServletRequest request) {
        User resUser = new User();
        BeanUtils.copyProperties(userVo, resUser);
        resUser = userService.modify(resUser, request);
        return ResultUtil.SUCCESS(resUser);
    }

    @RequestMapping("/add")
    public ResultUtil<User> register(@Validated RegisterVo registerVo) {
        boolean isExist = userService.isExist(registerVo.getMobile());
        if (isExist) {
            return ResultUtil.ERROR(CodeMsg.USER_EXIST);
        }
        User user = new User();
        BeanUtils.copyProperties(registerVo, user);
        if (user.getId() == null) {
            userService.doAddWithId(user);
        } else {
            userService.doAddWithOutId(user);
        }
        return ResultUtil.SUCCESS(user);
    }

    @RequestMapping("/search")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ResultUtil<User> search(@RequestParam(value = "page", required = true) Integer page,
                                   @RequestParam(value = "pageCount", defaultValue = "10") Integer pageCount) {
        //查询汇总
        PageVo<UserVo> pageVo = userService.search(page, pageCount);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("pageInfo", pageVo);
        resultMap.put("page", page);
        return ResultUtil.SUCCESS(resultMap);
    }

    @RequestMapping("/delete")
    @AccessLimit(needLogin = true, needPermission = true, permission = 3)
    public ResultUtil<String> delete(@NotNull Integer userId) {
        User byId = userService.getById(userId);
        if (byId == null) {
            return ResultUtil.SUCCESS("success");
        }
        userService.deleteById(userId);
        return ResultUtil.SUCCESS("success");
    }

    @RequestMapping("/login/all")
    public ResultUtil<String> loginAll() {
        redisService.cleanAll();
        UserUtil.loginAll();
        return ResultUtil.SUCCESS("success");
    }

    @RequestMapping("/multi/create")
    public ResultUtil<String> createUsers() {
        UserUtil.createUsers();
        return ResultUtil.SUCCESS("success");
    }
}
