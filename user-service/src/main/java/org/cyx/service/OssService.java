package org.cyx.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description OssService
 * @Author cyx
 * @Date 2021/2/16
 **/
public interface OssService {
    String uploadUserImg(MultipartFile file);
}
