package org.cyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.enums.ProductOrderPayTypeEnum;
import org.cyx.model.OrderMessage;
import org.cyx.model.ProductOrderDO;
import org.cyx.request.ConfirmOrderRequest;
import org.cyx.request.RepayOrderRequest;
import org.cyx.util.JsonData;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
public interface ProductOrderService extends IService<ProductOrderDO> {

    JsonData confirmOrder(ConfirmOrderRequest confirmOrderRequest);

    String queryProductState(String outTradeNo);

    boolean closeProductOrder(OrderMessage orderMessage);

    JsonData handlerOrderCallbackMsg(ProductOrderPayTypeEnum alipay, Map<String, String> params);

    Map<String, Object> listOrder(int page, int size, String state);

    JsonData repayOrder(RepayOrderRequest repayOrderRequest);
}
