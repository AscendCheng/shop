package org.cyx.service.impl;

import com.qcloud.cos.model.PutObjectResult;
import org.cyx.service.OssService;
import org.cyx.util.OssClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description OssServiceImp
 * @Author cyx
 * @Date 2021/2/16
 **/
@Service
public class OssServiceImp implements OssService {
    @Autowired
    public OssClient ossClient;

    @Override
    public String uploadUserImg(MultipartFile file) {
        PutObjectResult putObjectResult = ossClient.uploadFile(file,"shop-user-service-1258865434","user-head/");
        return putObjectResult == null ? null:putObjectResult.getCrc64Ecma();
    }
}
