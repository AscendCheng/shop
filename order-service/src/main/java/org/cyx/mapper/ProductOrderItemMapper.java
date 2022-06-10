package org.cyx.mapper;

import org.cyx.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-04-15
 */
@Repository
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {
    int insertAll(List<ProductOrderItemDO> list);
}
