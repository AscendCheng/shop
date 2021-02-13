package org.cyx.service.impl;

import org.cyx.model.AddressDO;
import org.cyx.mapper.AddressMapper;
import org.cyx.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电商-公司收发货地址表 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-02-08
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressDO> implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public AddressDO getDetail(String id) {
        return addressMapper.selectById(id);
    }
}
