package org.cyx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description PayInfoVo
 * @Author cyx
 * @Date 2021/7/9
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVo {
    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 订单总金额
     */
    private BigDecimal payFee;

    /**
     * 支付类型，微信/ 支付宝/其他
     */
    private String payType;

    /**
     * 端类型，APP/PC/H5
     */
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 支付超时时间毫秒
     */
    private double orderPayTimeoutMills;
}
