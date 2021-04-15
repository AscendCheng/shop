package org.cyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.model.UserDO;
import org.cyx.request.UserLoginRequest;
import org.cyx.request.UserRegisterRequest;
import org.cyx.util.JsonData;
import org.cyx.vo.UserVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
public interface UserService extends IService<UserDO> {
    JsonData register(UserRegisterRequest request);

    JsonData login(UserLoginRequest request);

    UserVo detail();
}
