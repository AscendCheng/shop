package org.cyx.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description ProductVO
 * @Author cyx
 * @Date 2021/4/7
 **/
@Data
public class ProductVO {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面图
     */
    @JsonProperty("cover_img")
    private String coverImg;

    /**
     * 详情
     */
    private String detail;

    /**
     * 老价格
     */
    @JsonProperty("oldPrice")
    private BigDecimal oldAmount;

    /**
     * 新价格
     */
    private BigDecimal amount;

    /**
     * 库存
     */
    private Integer stock;
}
