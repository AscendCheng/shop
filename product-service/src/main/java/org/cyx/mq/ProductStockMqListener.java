package org.cyx.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.cyx.model.ProductMessage;
import org.cyx.service.ProductService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description ProductStockMqListener
 * @Author cyx
 * @Date 2021/6/29
 **/
@Component
@Slf4j
public class ProductStockMqListener {
    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${mqconfig.stock_release_queue}")
    public void releaseCouponRecord(ProductMessage productMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息:{}", productMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productService.releaseProductStock(productMessage);
        try {
            if (flag) {
                channel.basicAck(msgTag, false);
            } else {
                log.error("释放商品库存失败:{}", productMessage);
                channel.basicReject(msgTag, true);
            }
        } catch (IOException e) {
            log.error("释放商品库存异常:{}", e, productMessage);
            channel.basicReject(msgTag, true);
        }
    }
}
