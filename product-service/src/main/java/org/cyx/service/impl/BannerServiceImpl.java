package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cyx.mapper.BannerMapper;
import org.cyx.model.BannerDO;
import org.cyx.service.BannerService;
import org.cyx.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@Service
public class BannerServiceImpl  implements BannerService {
    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<BannerVO> list(){
        List<BannerDO> doList = bannerMapper.selectList(new QueryWrapper<BannerDO>().orderByDesc("weight"));
        return doList.stream().map(this::beanProcess).collect(Collectors.toList());
    }

    private BannerVO beanProcess(BannerDO bannerDO){
        BannerVO bannerVO = new BannerVO();
        BeanUtils.copyProperties(bannerDO,bannerVO);
        return bannerVO;
    }
}
