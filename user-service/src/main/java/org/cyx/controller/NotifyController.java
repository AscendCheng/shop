package org.cyx.controller;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Description NotifyController
 * @Author cyx
 * @Date 2021/2/13
 **/
@RestController
@RequestMapping("/api/notify")
@Slf4j
public class NotifyController {
    @Autowired
    private Producer captchaProducer;

    @RequestMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String captcha = captchaProducer.createText();
        log.info("图形验证码:{}", captcha);
        BufferedImage bufferedImage = captchaProducer.createImage(captcha);
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", servletOutputStream);
        } catch (IOException e) {
            log.error("图形验证码异常");
        }
    }
}
