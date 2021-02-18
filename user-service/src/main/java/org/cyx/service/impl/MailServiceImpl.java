package org.cyx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cyx.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @Description MailServiceImpl
 * @Author cyx
 * @Date 2021/2/15
 **/
@Service
@Slf4j
public class MailServiceImpl implements MailService {
    // springboot 提供的发送邮件的简单抽象，直接注入即可
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendMail(String to, String subject, String content) {
        // 创建一个邮件消息对象
        SimpleMailMessage message = new SimpleMailMessage();
        //配置一下邮件的发送人
        message.setFrom(from);
        //配置邮件的接收人
        message.setTo(to);
        //邮件的主题
        message.setSubject(subject);
        //邮件的内容
        message.setText(content);
        mailSender.send(message);
        log.info("邮件发送成功，接收人:{}", to);
    }
}
