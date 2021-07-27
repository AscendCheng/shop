import lombok.extern.slf4j.Slf4j;
import org.cyx.OrderApplication;
import org.cyx.config.AlipayConfig;
import org.cyx.config.PayUrlConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description AlipayTest
 * @Author cyx
 * @Date 2021/7/8
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
@Slf4j
public class AlipayTest {
    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private PayUrlConfig payUrlConfig;

}
