package org.cyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.model.AddressDO;
import org.cyx.request.AddressRequest;
import org.cyx.util.JsonData;
import org.cyx.vo.AddressVo;

/**
 * <p>
 * 电商-公司收发货地址表 服务类
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
public interface AddressService extends IService<AddressDO> {
    JsonData listAddress();

    AddressVo getDetail(String id);

    JsonData add(AddressRequest addressRequest);

    JsonData del(String addressId);
}
