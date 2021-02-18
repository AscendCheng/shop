package org.cyx.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cyx.enums.BizCodeEnum;
import org.cyx.service.OssService;
import org.cyx.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description OssController
 * @Author cyx
 * @Date 2021/2/16
 **/
@RestController
@RequestMapping("/api/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    @ApiOperation("用户上传文件")
    @PostMapping("/upload")
    public JsonData upload(@ApiParam(value = "文件上传", required = true) @RequestPart("file") MultipartFile file) {
        String result = ossService.uploadUserImg(file);
        return result == null ? JsonData.buildError(BizCodeEnum.OSS_UPLOAD_FAIL.getMessage()) : JsonData.buildSuccess(result);
    }
}
