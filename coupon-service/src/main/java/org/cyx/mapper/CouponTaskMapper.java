package org.cyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.cyx.model.CouponTaskDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-05-18
 */
@Repository
public interface CouponTaskMapper extends BaseMapper<CouponTaskDO> {
    int insertBatch(@Param("couponTaskList") List<CouponTaskDO> couponTaskDOList);
}
