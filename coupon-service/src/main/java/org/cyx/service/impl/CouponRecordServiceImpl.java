package org.cyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.mapper.CouponRecordMapper;
import org.cyx.model.CouponRecordDO;
import org.cyx.model.LoginUser;
import org.cyx.service.CouponRecordService;
import org.cyx.vo.CouponRecordVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
@Service
public class CouponRecordServiceImpl extends ServiceImpl<CouponRecordMapper, CouponRecordDO> implements CouponRecordService {
    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Override
    public Map<String, Object> page(int page, int size) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        Page<CouponRecordDO> pageInfo = new Page<>(page, size);
        IPage<CouponRecordDO> couponRecordDOPage =
                couponRecordMapper.selectPage(pageInfo, new QueryWrapper<CouponRecordDO>()
                        .eq("user_id", loginUser.getId()).orderByDesc("create_time"));
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("total_record", couponRecordDOPage.getTotal());
        pageMap.put("total_page", couponRecordDOPage.getPages());
        pageMap.put("current_data", couponRecordDOPage.getRecords().stream()
                .map(couponRecordDO -> beanProcess(couponRecordDO)).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public CouponRecordVo findById(Long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(
                new QueryWrapper<CouponRecordDO>().eq("id",recordId).eq("user_id",loginUser.getId()));
        if(couponRecordDO == null){
             return null;
        }
        return beanProcess(couponRecordDO);
    }

    private CouponRecordVo beanProcess(CouponRecordDO couponRecordDO) {
        CouponRecordVo couponRecordVo = new CouponRecordVo();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVo);
        return couponRecordVo;
    }
}
