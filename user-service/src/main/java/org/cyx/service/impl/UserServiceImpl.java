package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.SendCodeEnum;
import org.cyx.feign.CouponFeignService;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.mapper.UserMapper;
import org.cyx.model.LoginUser;
import org.cyx.model.UserDO;
import org.cyx.request.NewUserCouponRequest;
import org.cyx.request.UserLoginRequest;
import org.cyx.request.UserRegisterRequest;
import org.cyx.service.NotifyService;
import org.cyx.service.UserService;
import org.cyx.util.CommonUtil;
import org.cyx.util.JWTUtil;
import org.cyx.util.JsonData;
import org.cyx.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    @Autowired
    private NotifyService notifyService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    //@GlobalTransactional
    public JsonData register(UserRegisterRequest request) {
        boolean checkCode = false;
        if (StringUtils.isNoneBlank(request.getMail())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, request.getMail(),
                    request.getCode());
        }
        if (!checkCode) {
            return JsonData.buildError(BizCodeEnum.CODE_ERROR.getMessage());
        }
        if (checkUnique(request.getMail())) {
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(request, userDO);
            userDO.setCreateTime(new Date());

            // 生成盐
            userDO.setSecret("$1$" + CommonUtil.getRandomString(8));
            String cryptPwd = Md5Crypt.md5Crypt(request.getPwd().getBytes(StandardCharsets.UTF_8), userDO.getSecret());
            userDO.setPwd(cryptPwd);
            int rows = userMapper.insert(userDO);
            log.info("rows:{},用户注册成功:{}", rows, userDO.toString());
            userRegisterInitTask(userDO);

            return JsonData.buildSuccess(BizCodeEnum.OPS_SUCCESS.getMessage());
        }
        return JsonData.buildError(BizCodeEnum.ACCOUNT_REPEAT.getMessage());
    }

    private void userRegisterInitTask(UserDO userDO) {
        NewUserCouponRequest newUserCouponRequest = new NewUserCouponRequest();
        newUserCouponRequest.setUserId(userDO.getId());
        newUserCouponRequest.setName(userDO.getName());
        JsonData jsonData = couponFeignService.addNewUserCoupon(newUserCouponRequest);
        log.info("发放新用户:{}，结果:{}", userDO.getName(), jsonData.getMsg());
    }

    private boolean checkUnique(String mail) {
        // 万分之一的时间放大100万倍
        // 代码并非安全的只是并发量过少无法发现问题
        // 高并发难模拟：1.代码暂停思维 2.时间扩大思维
        // 这里可以利用数据库唯一索引
        return userMapper.selectOne(new QueryWrapper<UserDO>().eq("mail", mail)) == null;
    }

    @Override
    public JsonData login(UserLoginRequest request) {
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("mail", request.getMail()));
        if (userDO == null) {
            return JsonData.buildError(BizCodeEnum.ACCOUNT_UNREGISTER.getMessage());
        }
        String cryptPwd = Md5Crypt.md5Crypt(request.getPwd().getBytes(StandardCharsets.UTF_8), userDO.getSecret());
        if (userDO.getPwd().equals(cryptPwd)) {
            LoginUser loginUser = LoginUser.builder().build();
            BeanUtils.copyProperties(userDO, loginUser);
            return JsonData.buildSuccess(JWTUtil.generationJWT(loginUser));
        }
        return JsonData.buildError(BizCodeEnum.ACCOUNT_PWD_ERROR.getMessage());
    }

    @Override
    public UserVo detail() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        UserDO userDo = userMapper.selectOne(new QueryWrapper<UserDO>().eq("id", loginUser.getId()));
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userDo, userVo);
        return userVo;
    }


}
