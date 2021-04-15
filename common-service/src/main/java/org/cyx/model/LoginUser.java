package org.cyx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description LoginUser
 * @Author cyx
 * @Date 2021/2/19
 **/
@Data
@Builder
public class LoginUser {
    private Long id;

    private String name;

    @JsonProperty("head_img")
    private String headImg;

    private String mail;
}
