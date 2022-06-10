package org.cyx.mapper;

import org.cyx.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@Repository
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {

    void updateOrderPayState(String outTradeNo, String newState, String oldState);
}
