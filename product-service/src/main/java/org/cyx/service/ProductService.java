package org.cyx.service;

import org.cyx.model.ProductDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.model.ProductMessage;
import org.cyx.request.LockProductRequest;
import org.cyx.util.JsonData;
import org.cyx.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
public interface ProductService extends IService<ProductDO> {
    Map<String,Object> page(int page,int size);

    ProductVO findDetailById(String id);

    List<ProductVO> findDetailByIdBatch(List<Long> productIdList);

    JsonData lockProduct(LockProductRequest lockProductRequest);

    boolean releaseProductStock(ProductMessage productMessage);
}
