package org.cyx.service;

/**
 * @Description MailService
 * @Author cyx
 * @Date 2021/2/15
 **/
public interface MailService {
    void sendMail(String to, String subject, String content);
}
