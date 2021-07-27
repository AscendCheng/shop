package org.cyx.model;

import lombok.Data;

/**
 * @Description CouponRecordMessage
 * @Author cyx
 * @Date 2021/6/8
 **/
@Data
public class CouponRecordMessage {
    private String messageId;

    private String outTradeNo;

    private Long taskId;
}
