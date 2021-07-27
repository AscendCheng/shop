package org.cyx.mapper;

import org.apache.ibatis.annotations.Param;
import org.cyx.model.CouponRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Repository
public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {
    int lockUseStateBatch(@Param("userId") Long userId, @Param("useState") String useState, @Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);

    int updateState(@Param("couponRecordId") Long couponRecordId, @Param("useState") String useState);
}
