package org.cyx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cyx.constant.CacheKey;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.SendCodeEnum;
import org.cyx.service.MailService;
import org.cyx.service.NotifyService;
import org.cyx.util.CheckUtil;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description NotifyServiceImpl
 * @Author cyx
 * @Date 2021/2/15
 **/
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {
    private static final String REGISTER_SUBJECT = "注册验证码";

    private static final String REGISTER_CONTENT = "您的验证码为：%s（泄露有风险），有效时间10分钟！";

    private static final Integer CODE_EXPIRE_TIME = 60;


    @Autowired
    private MailService mailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        boolean success = false;
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            if (CommonUtil.getCurrentTimeStamp() - ttl < 1000 * 60) {
                log.error("to：{},发送验证码过快！");
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }
        if (sendCodeEnum != null && (CheckUtil.isEmail(to) || CheckUtil.isPhone(to))) {
            success = chooseSend(sendCodeEnum, to);
        }
        return success ? JsonData.buildSuccess(BizCodeEnum.OPS_SUCCESS.getMessage()) :
                JsonData.buildError(BizCodeEnum.OPS_FAILE.getMessage());
    }

    private boolean chooseSend(SendCodeEnum sendCodeEnum, String to) {
        boolean success = false;
        switch (sendCodeEnum) {
            case USER_REGISTER:
                success = registerSend(to);
                break;
            default:
                log.error("错误的类型");
                success = false;
                break;
        }
        return success;
    }

    private boolean registerSend(String to) {
        String code = CommonUtil.getRandomCode(6);
        String value = code + "_" + CommonUtil.getCurrentTimeStamp();
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER.name(), to);
        redisTemplate.opsForValue().set(cacheKey, value, CODE_EXPIRE_TIME, TimeUnit.SECONDS);
        String content = String.format(REGISTER_CONTENT, code);
        log.info("验证码：{}",content);
        // mailService.sendMail(to, REGISTER_SUBJECT, content);
        return true;
    }

    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            String cacheCode = cacheValue.split("_")[0];
            if(cacheCode.equals(code)){
                redisTemplate.delete(cacheKey);
                return true;
            }
        }
        return false;
    }
}
