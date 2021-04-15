package org.cyx.service.impl;

import org.cyx.model.ProductOrderDO;
import org.cyx.mapper.ProductOrderMapper;
import org.cyx.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrderDO> implements ProductOrderService {

}
