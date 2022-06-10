package mail;

import lombok.extern.slf4j.Slf4j;
import org.cyx.UserApplication;
import org.cyx.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description MailTest
 * @Author cyx
 * @Date 2021/2/15
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class MailTest {
    @Autowired
    private MailService mailService;

    @Test
    public void testSendMail() {
        mailService.sendMail("2652555476@qq.com", "测试邮件", "哈哈哈");
    }
}
