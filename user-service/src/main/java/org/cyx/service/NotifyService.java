package org.cyx.service;

import org.cyx.enums.SendCodeEnum;
import org.cyx.util.JsonData;

public interface NotifyService {
    JsonData sendCode(SendCodeEnum sendCodeEnum,String to);

    boolean checkCode(SendCodeEnum sendCodeEnum,String to,String code);

}
