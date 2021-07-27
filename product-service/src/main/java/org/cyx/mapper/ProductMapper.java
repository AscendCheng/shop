package org.cyx.mapper;

import org.apache.ibatis.annotations.Param;
import org.cyx.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@Repository
public interface ProductMapper extends BaseMapper<ProductDO> {

    int lockProductStock(@Param("productId") long productId, @Param("buyNum") int buyNum);

    int unlockProductStock(@Param("productId") Long productId,@Param("buyNum") int buyNum);
}
