package org.cyx.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Configuration;

/**
 * @Description AlipayConfig
 * @Author cyx
 * @Date 2021/7/7
 **/
@Configuration
public class AlipayConfig {
    /**
     * 应用私钥
     */
    public static final String APP_PRI_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCA2cYG2imsD2PujeBe8lHs+p" +
            "2VV1Rkn1hBfZ0C7P9mIMZSf3zV225BvxNc/OTULtFB6lT4iEv8OI0u2oPKRskloUvEE19N7fWVOWUqPNBSqA6gaVFkFPvZliRZnNdZrtKN" +
            "lRGceKhbGTsnydVT0EUGwn3FTc/adAjVFiUmTBJFAGoGQCg1cmCrg7WOzDdeI4Y1J9+Pn7+K+A8I+cRYsPm09FeohYJ3jN49qL5Vkl8daIU" +
            "6r4BzeiWvrhtrIUYEkevLiSD02V6CGS4YxilVzrl8K2LcWCmgyF1LRIbI1Zlvy5O4ECmp5X6+KQ0M27dipcBqsERerAo37SE1NMos49JTAg" +
            "MBAAECggEAQWG/6WQ0dxmMJNWbP7KIPc7hvZEFwtVUbE74KC7oXgNNfzkvuDjTVstFYQ72cnCbQG5O1ZobrI1pnpfE/hQzJ0QUdRc7agjQb/siy" +
            "W74aqjaWy113moDbeKQ6WP7podeUfeNay+Shj9kVAlHlXL+Q7LXLRIR4ZC1lqLZEPjS4G3cuOua2Uh0P5FcvdKNBH8rvuw2E5q3OHTy3khdLU+yj99u" +
            "LqCyNJ+iJCTdQayHjaXFL3Yyf8Dg1DxW2yJhcHA3tCw12+Bh7yMz0llULSVfHI/dLIlQuAczRK1yJtXbrBEt/Pe3+67q85JRDul4yf7hj" +
            "2985qqVkBO8Nvcm4Mue6QKBgQDbk7Oytsm43/Ms/OLEomTRHlQ5xyK9njwPIZsMbfyt6NslOcCdMCTDQM2a6XF/WmE1HQsGD1MSorlPz0Re+lrvGjB/" +
            "wSBhorA7zHzcU1C+JGowXHvFxX5ywgdteg7K/mTLrwzNvppzYjKsEE0lyu51CNhE+GnqZ3/qudGGsuq19wKBgQCWOWVBHikxruZfXFS46Kd3CL1Us" +
            "WFXiKxcv6lVHUQso4Gm54O9ghUu81lMnlBcoZXn7ZSS+rOHf6344qcYm/sUVpoONsrTYjj46ApnQ6b430mWHBMRUKWRPTrcKtWJYkyJxZxjyJFcjg89JHmh2O/" +
            "PP4CPZhPXcnA2e/pJMlC/hQKBgHAt0564N/LVp10SjaA7/xm/TvR9wkfxsWlhl98Pygnnbx5jlP45fG6sit5b6XpQ6FzES7960Mwyik417A5CB" +
            "XcmgCSkvDBld3f1jszZW+Toob+3YBy4O1PRwQ2zUB9xPHm7IuVyPWI5hv3VDjUCqjTsoeEKsMHqwrVe3abdfYAdAoGAEw2EghXJM7vzC/Ux6aHYW4i" +
            "IOs2kgu2arsjAOUNU+jRipUCHvOlatnG2ybjqiFUWJixDw8B4NJ9E+WQFvWFLHvE86MCaSOUURL1pVF+INdhcBOXapT+NQDZ72KC2JI6LFo4xn5Y4mXyLA" +
            "j42DwjsoWR0cbuImLKy+LYQsGfpReECgYEAvs/asq4UTwx3cvkAii2WN34oK6Svsz1G7XoeRV12NePxd67Y6xb5IJZ4xA1JJ8yuWavpLsGN4GOtuwPfpgJj" +
            "Bf2IZRdq52ZeEy/NZq/7CWts2tesMr+jqQCQ8jbWQYmd67OxYq476MxO/c44aXxD0ov3V+97vNg4xaE7AD2D0Qw=";

    /**
     * 支付宝公钥
     */
    public static final String ALIPAY_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA68St4zyaqYiGNQWhBBHd0RMQf" +
            "ofsvseSHBW6tp4RAxrPRKkuHoxO+2UiVpSXPPri33+SAHsxRjTAA78NzNwreY2YU+mYpNg1y+HYgAAtfOxFfffQEQGQUAvG0uamPbIHeFM" +
            "BQE4qKBNzJQOhqKhSz3DifbWsoLP0FKojm+UYILSwFoEYDsbt2vyEGtEK5UOs1ruHvhZLs0js9r1cK3JgQX7kr1WIkeLyN7FdIelu0b+5Ju" +
            "FFYWR1ZhDiHSKx2JPew7I/vcJeFvgcQjF3YVqsVCVOqjvtlq67lhkYGbBkSIu/ittfYtj5QgBnONt4dMsnqRpOkZ75ydU5LGfnqPonRQIDAQAB";

    /**
     * 应用ID
     */
    public static final String APP_ID = "2021000117685187";

    /**
     * 签名类型
     */
    public static final String SIGN_TYPE = "RSA2";

    /**
     * 编码类型
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 网关地址
     */
    public static final String PAY_GATEWAY = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 格式化
     */
    public static final String FORMATE = "JSON";

    public AlipayConfig() {
    }

    private volatile static AlipayClient instance = null;

    public static AlipayClient alipayClient() {
        if (instance == null) {
            synchronized (AlipayConfig.class) {
                if (instance == null) {
                    instance = new DefaultAlipayClient(PAY_GATEWAY, APP_ID, APP_PRI_KEY, FORMATE, CHARSET, ALIPAY_PUB_KEY, SIGN_TYPE);
                }
            }
        }
        return instance;
    }
}
