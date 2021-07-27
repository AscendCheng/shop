package org.cyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cyx.model.CouponRecordDO;
import org.cyx.model.CouponRecordMessage;
import org.cyx.request.LockCouponRecordRequest;
import org.cyx.util.JsonData;
import org.cyx.vo.CouponRecordVo;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cyx
 * @since 2021-03-04
 */
public interface CouponRecordService extends IService<CouponRecordDO> {
    Map<String,Object> page(int page,int size);

    CouponRecordVo findById(Long recordId);

    JsonData lockCouponRecords(LockCouponRecordRequest lockCouponRecordRequest);

    boolean releaseCouponRecord(CouponRecordMessage couponRecordMessage);
}
