package org.cyx.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description MqConfig
 * @Author cyx
 * @Date 2021/6/2
 **/
@Configuration
@Data
public class MqConfig {
    /**
     * 交换机
     */
    @Value("${mqconfig.order_event_exchange}")
    private String orderEventExchange;

    /**
     * 第一个队列延迟队列，
     */
    @Value("${mqconfig.order_close_delay_queue}")
    private String orderCloseDelayQueue;

    /**
     * 第一个队列的路由key
     * 进入队列的路由key
     */
    @Value("${mqconfig.order_close_delay_routing_key}")
    private String orderCloseDelayRoutingKey;

    /**
     * 第二个队列，被监听恢复库存的队列
     */
    @Value("${mqconfig.order_close_queue}")
    private String orderCloseQueue;

    /**
     * 第二个队列的路由key
     * <p>
     * 即进入死信队列的路由key
     */
    @Value("${mqconfig.order_close_routing_key}")
    private String orderCloseRoutingKey;

    /**
     * 过期时间
     */
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange couponEventExchange() {
        return new TopicExchange(orderEventExchange, true, false);
    }

    @Bean
    public Queue orderCloseDelayQueue() {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("x-message-ttl", ttl);
        argsMap.put("x-dead-letter-routing-key", orderCloseRoutingKey);
        argsMap.put("x-dead-letter-exchange", orderEventExchange);
        return new Queue(orderCloseDelayQueue, true, false, false, argsMap);
    }

    @Bean
    public Queue orderCloseQueue() {
        return new Queue(orderCloseQueue, true, false, false);
    }

    @Bean
    public Binding orderCloseBinding() {
        return new Binding(orderCloseQueue, Binding.DestinationType.QUEUE, orderEventExchange, orderCloseRoutingKey, null);
    }

    @Bean
    public Binding couponReleaseDelayBinding() {
        return new Binding(orderCloseDelayQueue, Binding.DestinationType.QUEUE, orderEventExchange, orderCloseDelayRoutingKey, null);
    }
}
