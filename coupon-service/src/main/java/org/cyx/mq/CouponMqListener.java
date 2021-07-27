package org.cyx.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.cyx.model.CouponRecordMessage;
import org.cyx.service.CouponRecordService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description CouponMqListener
 * @Author cyx
 * @Date 2021/6/8
 **/
@Component
@Slf4j
public class CouponMqListener {
    @Autowired
    private CouponRecordService couponRecordService;

    @RabbitListener(queues = "${mqconfig.coupon_release_queue}")
    public void releaseCouponRecord(CouponRecordMessage couponRecordMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息:{}", couponRecordMessage);
        long msgTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = couponRecordService.releaseCouponRecord(couponRecordMessage);
        try {
            if (flag) {
                channel.basicAck(msgTag, false);
            } else {
                log.error("释放优惠券记录失败:{}", couponRecordMessage);
                channel.basicReject(msgTag, true);
            }
        } catch (IOException e) {
            log.error("释放优惠券记录异常:{}", e, couponRecordMessage);
            channel.basicReject(msgTag, true);
        }
    }
}
