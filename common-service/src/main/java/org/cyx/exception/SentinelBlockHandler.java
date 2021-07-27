package org.cyx.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import org.cyx.enums.BizCodeEnum;
import org.cyx.util.CommonUtil;
import org.cyx.util.JsonData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description SentinelBlockHandler
 * @Author cyx
 * @Date 2021/7/26
 **/
@Component
public class SentinelBlockHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        JsonData jsonData = null;
        if(e instanceof FlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_FLOW);
        }else if(e instanceof FlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_DEGRADE);
        }else if(e instanceof FlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.CONTROL_AUTH);
        }
        httpServletResponse.setStatus(200);
        CommonUtil.sengMsg(httpServletResponse,jsonData);
    }
}
