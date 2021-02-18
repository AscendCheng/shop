package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.SendCodeEnum;
import org.cyx.mapper.UserMapper;
import org.cyx.model.UserDO;
import org.cyx.request.UserRegisterRequest;
import org.cyx.service.NotifyService;
import org.cyx.service.UserService;
import org.cyx.util.JsonData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
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
            int rows = userMapper.insert(userDO);
            log.info("rows:{},用户注册成功:{}", rows, userDO.toString());
            userRegisterInitTask(userDO);
            return JsonData.buildSuccess(BizCodeEnum.OPS_SUCCESS.getMessage());
        }
        return JsonData.buildError(BizCodeEnum.ACCOUNT_REPEAT.getMessage());
    }

    private void userRegisterInitTask(UserDO userDO) {

    }

    private boolean checkUnique(String mail) {
        return userMapper.selectOne(new QueryWrapper<UserDO>().eq("mail", mail)) == null;
    }
}
