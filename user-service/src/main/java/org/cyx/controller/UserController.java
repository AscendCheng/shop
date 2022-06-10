package org.cyx.controller;


import io.swagger.annotations.ApiOperation;
import org.cyx.request.UserLoginRequest;
import org.cyx.request.UserRegisterRequest;
import org.cyx.service.UserService;
import org.cyx.util.JsonData;
import org.cyx.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public JsonData register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public JsonData login(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest);
    }

    @ApiOperation("用户详情")
    @GetMapping("/detail")
    public UserVo detail() {
        return userService.detail();
    }
}

