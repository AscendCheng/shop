package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cyx.mapper.ProductMapper;
import org.cyx.model.ProductDO;
import org.cyx.service.ProductService;
import org.cyx.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductDO> implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> page(int page, int size) {
        Page<ProductDO> pageInfo = new Page<>(page, size);
        IPage<ProductDO> productDOIPage = productMapper.selectPage(pageInfo, null);
        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", productDOIPage.getTotal());
        pageMap.put("total_page", productDOIPage.getPages());
        pageMap.put("current_data", productDOIPage.getRecords()
                .stream().map(this::beanProcess).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public ProductVO findDetailById(String id) {
        ProductDO productDO = productMapper.selectById(id);
        return beanProcess(productDO);
    }

    @Override
    public List<ProductVO> findDetailByIdBatch(List<Long> productIdList) {
        List<ProductDO> productDOList = productMapper.selectList(new QueryWrapper<ProductDO>().in("id",productIdList));
        return productDOList.stream().map(this::beanProcess).collect(Collectors.toList());
    }

    private ProductVO beanProcess(ProductDO productDO) {
        if(productDO == null){
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        return productVO;
    }
}
