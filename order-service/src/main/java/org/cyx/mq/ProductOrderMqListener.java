package org.cyx.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.cyx.model.OrderMessage;
import org.cyx.service.ProductOrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description ProductOrderMqListener
 * @Author cyx
 * @Date 2021/7/1
 **/
@Slf4j
@Component
public class ProductOrderMqListener {
    @Autowired
    private ProductOrderService productOrderService;

    @RabbitListener(queues = "${mqconfig.order_close_queue}")
    public void closeProductOrder(OrderMessage orderMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息：closeProductOrder:{}", orderMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();
        try {
            boolean flag = productOrderService.closeProductOrder(orderMessage);
            if (flag) {
                channel.basicAck(msgTag, false);
            } else {
                channel.basicReject(msgTag, false);
            }
        } catch (Exception e) {
            log.error("定时关单失败", orderMessage);
            channel.basicReject(msgTag, false);
        }
    }

}
