package org.cyx.model;

import lombok.Data;

/**
 * @Description OrderMessage
 * @Author cyx
 * @Date 2021/6/25
 **/
@Data
public class OrderMessage {
    private Long messageId;

    private String outTradeNo;
}
