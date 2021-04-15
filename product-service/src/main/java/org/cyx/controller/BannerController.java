package org.cyx.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.cyx.service.BannerService;
import org.cyx.util.JsonData;
import org.cyx.vo.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cyx
 * @since 2021-04-07
 */
@Api("轮播图模块")
@RestController
@RequestMapping("/api/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @ApiOperation("轮播图列表")
    @GetMapping("/list")
    public JsonData list(){
        List<BannerVO> result = bannerService.list();
        return JsonData.buildSuccess(result);
    }

}

