package org.cyx.vo;

import lombok.Data;

/**
 * @Description BannerVO
 * @Author cyx
 * @Date 2021/4/7
 **/
@Data
public class BannerVO {
    private Integer id;

    /**
     * 图片
     */
    private String img;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 权重
     */
    private Integer weight;


}
