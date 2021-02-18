package org.cyx.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.SendCodeEnum;
import org.cyx.service.NotifyService;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description NotifyController
 * @Author cyx
 * @Date 2021/2/13
 **/
@RestController
@RequestMapping("/api/notify")
@Slf4j
public class NotifyController {
    private static final Integer CAPTCHA_EXPIRED_TIME = 60 * 10;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private NotifyService notifyService;

    @RequestMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String captcha = captchaProducer.createText();
        log.info("图形验证码:{}", captcha);
        redisTemplate.opsForValue().set(getCaptchaKey(request), captcha, CAPTCHA_EXPIRED_TIME, TimeUnit.SECONDS);
        BufferedImage bufferedImage = captchaProducer.createImage(captcha);
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", servletOutputStream);
        } catch (IOException e) {
            log.error("图形验证码异常");
        }
    }

    /**
     * 获取缓存的key
     */
    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "user-service:captcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("ip:{},userAgent:{},key:{}", ip, userAgent, key);
        return key;
    }

    @ApiOperation("发送邮箱验证码")
    @GetMapping("/sendCode")
    public JsonData sendRegisterCode(@RequestParam(value = "to", required = true) String to,
                                     @RequestParam("captcha") String captcha, HttpServletRequest request) {
        String key = getCaptchaKey(request);
        String cacheCaptcha = redisTemplate.opsForValue().get(key);
        // 匹配验证码是否一致
        if (captcha != null && cacheCaptcha != null && captcha.equalsIgnoreCase(cacheCaptcha)) {
            // 成功
            redisTemplate.delete(key);
            JsonData jsonData = notifyService.sendCode(SendCodeEnum.USER_REGISTER, to);
            return jsonData;
        } else {
            return JsonData.buildError(BizCodeEnum.CODE_ERROR.getMessage());
        }
    }
}
