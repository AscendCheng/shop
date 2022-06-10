package org.cyx.service.impl;

import org.cyx.model.ProductOrderItemDO;
import org.cyx.mapper.ProductOrderItemMapper;
import org.cyx.service.ProductOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@Service
public class ProductOrderItemServiceImpl extends ServiceImpl<ProductOrderItemMapper, ProductOrderItemDO> implements ProductOrderItemService {

}
