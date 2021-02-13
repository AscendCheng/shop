package org.cyx.exception;

import lombok.Data;
import org.cyx.enums.BizCodeEnum;

/**
 * @Description BizException
 * @Author cyx
 * @Date 2021/2/10
 **/
@Data
public class BizException extends RuntimeException {
    private int code;
    private String msg;

    public BizException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BizException(BizCodeEnum enums) {
        this.code = enums.getCode();
        this.msg = enums.getMessage();
    }
}
