package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cyx.enums.AddressStatusEnum;
import org.cyx.enums.BizCodeEnum;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.model.AddressDO;
import org.cyx.mapper.AddressMapper;
import org.cyx.model.LoginUser;
import org.cyx.request.AddressRequest;
import org.cyx.service.AddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cyx.util.JsonData;
import org.cyx.vo.AddressVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public AddressVo getDetail(String id) {
        AddressDO addressDO =  addressMapper.selectById(id);
        AddressVo addressVo = new AddressVo();
        BeanUtils.copyProperties(addressDO,addressVo);
        return addressVo;
    }

    @Override
    public JsonData add(AddressRequest addressRequest) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        AddressDO addressDO = new AddressDO();
        addressDO.setCreateTime(new Date());
        addressDO.setUserId(loginUser.getId());
        BeanUtils.copyProperties(addressRequest, addressDO);

        if (addressDO.getDefaultStatus() == AddressStatusEnum.DEFAULT_ADDRESS.getCode()) {
            AddressDO defaultAddressDo = addressMapper.selectOne(
                    new QueryWrapper<AddressDO>()
                            .eq("user_id", loginUser.getId()).
                            eq("default_status", AddressStatusEnum.DEFAULT_ADDRESS.getCode()));
            if (defaultAddressDo != null) {
                defaultAddressDo.setDefaultStatus(AddressStatusEnum.NOT_DEFAULT_ADDRESS.getCode());
                addressMapper.update(defaultAddressDo,
                        new QueryWrapper<AddressDO>().eq("id", defaultAddressDo.getId()));
            }
        }
        int rows = addressMapper.insert(addressDO);
        return rows > 0 ? JsonData.buildSuccess() : JsonData.buildError(BizCodeEnum.OPS_FAILE.getMessage());
    }

    @Override
    public JsonData del(String addressId) {
        int rows = addressMapper.deleteById(addressId);
        return rows > 0 ? JsonData.buildSuccess() : JsonData.buildError(BizCodeEnum.OPS_FAILE.getMessage());
    }

}
