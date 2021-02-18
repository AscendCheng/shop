package org.cyx.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;

/**
 * @Description OssUtil
 * @Author cyx
 * @Date 2021/2/16
 **/
@Component
public class OssBuilder {
    public COSClient build(String secretId, String secretKey, String regionUrl) {
        // 初始化用户信息
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 设置Bucket区域
        Region region = new Region(regionUrl);
        ClientConfig clientConfig = new ClientConfig(region);
        // 生成cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }
}
