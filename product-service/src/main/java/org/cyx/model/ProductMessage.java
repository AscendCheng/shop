package org.cyx.model;

import lombok.Data;

/**
 * @Description ProductMessage
 * @Author cyx
 * @Date 2021/6/29
 **/
@Data
public class ProductMessage {
    private long messageId;
    private String outTradeNo;
    private long taskId;
}
