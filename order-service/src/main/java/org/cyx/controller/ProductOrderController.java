package org.cyx.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cyx.config.AlipayConfig;
import org.cyx.config.PayUrlConfig;
import org.cyx.constant.CacheKey;
import org.cyx.enums.BizCodeEnum;
import org.cyx.enums.ProductOrderPayTypeEnum;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.model.LoginUser;
import org.cyx.request.ConfirmOrderRequest;
import org.cyx.request.RepayOrderRequest;
import org.cyx.service.ProductOrderService;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class ProductOrderController {
    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private PayUrlConfig payUrlConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ApiOperation("/提交订单")
    @PostMapping("/confirm")
    public JsonData confirmOrder(@RequestBody ConfirmOrderRequest confirmOrderRequest, HttpServletResponse response) {
        JsonData jsonData = productOrderService.confirmOrder(confirmOrderRequest);
        if (jsonData.getCode() == 0) {
            String payType = confirmOrderRequest.getPayType();
            if (ProductOrderPayTypeEnum.ALIPAY.name().equals(payType)) {
                writeData(jsonData, response);
            }
        }
        return jsonData;
    }

    @ApiOperation("/重新支付")
    @PostMapping("/repay")
    public JsonData repay(@RequestBody RepayOrderRequest repayOrderRequest, HttpServletResponse response) {
        JsonData jsonData = productOrderService.repayOrder(repayOrderRequest);
        if (jsonData.getCode() == 0) {
            String payType = repayOrderRequest.getPayType();
            if (ProductOrderPayTypeEnum.ALIPAY.name().equals(payType)) {
                writeData(jsonData, response);
            }
        }
        return jsonData;
    }

    @ApiOperation("查询订单状态")
    @GetMapping("/queryState")
    public JsonData queryProductOrderState(@ApiParam("订单号") @RequestParam("out_trade_no") String outTradeNo) {
        String state = productOrderService.queryProductState(outTradeNo);
        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);
    }

    @ApiOperation("/订单列表")
    @GetMapping("/list")
    public Map<String, Object> list(
            @ApiParam(value = "页", required = true) @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "页大小", required = true) @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "状态", required = false) @RequestParam(value = "state") String state) {
        return productOrderService.listOrder(page, size, state);
    }


    private void writeData(JsonData jsonData, HttpServletResponse response) {
        try {
            response.setContentType("text/html;charset=UTF8");
            response.getWriter().write(jsonData.getData().toString());
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("订单返回异常", e);
        }
    }

    @GetMapping("/testAlipay")
    public void testAlipay(HttpServletResponse httpServletResponse) throws IOException, AlipayApiException {
        Map<String, String> content = new HashMap<>();
        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String no = UUID.randomUUID().toString();
        log.info("订单号:{}", no);
        content.put("out_trade_no", no);
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //订单总金额，单位为元，精确到小数点后两位
        content.put("total_amount", String.valueOf("111.99"));
        //商品标题/交易标题/订单标题/订单关键字等。 注意：不可使用特殊字符，如 /，=，&amp; 等。
        content.put("subject", "杯子");
        //商品描述，可空
        content.put("body", "好的杯子");
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        content.put("timeout_express", "5m");

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

        AlipayTradeWapPayResponse response = AlipayConfig.alipayClient().pageExecute(request);
        ;
        if (response.isSuccess()) {
            System.out.println("调用成功");
            String form = response.getBody();
            httpServletResponse.setContentType("text/html;charset=utf-8");
            httpServletResponse.getWriter().write(form);
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        } else {
            System.out.println("调用失败");
        }
    }

    @ApiOperation("/获取提交订单令牌")
    @GetMapping("/orderToken")
    public JsonData getOrderToken() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String cacheKey = String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUser.getId());
        String token = CommonUtil.getRandomString(32);
        redisTemplate.opsForValue().set(cacheKey, token, 30, TimeUnit.MINUTES);
        return JsonData.buildSuccess(token);
    }
}

