package org.cyx.mapper;

import org.cyx.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Repository
public interface CouponMapper extends BaseMapper<CouponDO> {
    int reduceStock(long couponId);
}
