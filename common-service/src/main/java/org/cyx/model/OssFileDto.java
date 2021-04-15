package org.cyx.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description FileDto
 * @Author cyx
 * @Date 2021/2/16
 **/
@Getter
@Setter
public class OssFileDto {
    private long fileSize;
    private String key;
    private String etag;
    private String storageClasses;

    public OssFileDto(){
    }

    public OssFileDto(long fileSize, String key, String etag, String storageClasses) {
        this.fileSize = fileSize;
        this.key = key;
        this.etag = etag;
        this.storageClasses = storageClasses;
    }
}
