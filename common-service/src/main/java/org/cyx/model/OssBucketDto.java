package org.cyx.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description BucketDto
 * @Author cyx
 * @Date 2021/2/16
 **/
@Getter
@Setter
public class OssBucketDto {
    private String bucketName;
    private String bucketLocation;

    public OssBucketDto(String bucketName, String bucketLocation) {
        this.bucketName = bucketName;
        this.bucketLocation = bucketLocation;
    }
}
