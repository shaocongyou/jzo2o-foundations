package com.jzo2o.foundations.controller.consumer;

import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("consumerServeController")
@RequestMapping("/customer/serve")
@Api(tags = "用户端 - 首页服务查询接口")
public class FirstPageServeController  {

    @Resource
    private HomeService homeService;

    @GetMapping("/firstPageServeList")
    @ApiOperation("首页服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeCategoryResDTO> serveCategory(@RequestParam("regionId") Long regionId) {
        List<ServeCategoryResDTO> serveCategoryResDTOS = homeService.queryServeIconCategoryByRegionIdCache(regionId);
        return serveCategoryResDTOS;
    }

    @GetMapping("/serveTypeList")
    @ApiOperation("服务分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(@RequestParam("regionId") Long regionId) {
        List<ServeAggregationTypeSimpleResDTO> serveAggregationTypeSimpleResDTOs = homeService.serveTypeList(regionId);
        return serveAggregationTypeSimpleResDTOs;
    }

    @GetMapping("/hotServeList")
    @ApiOperation("首页热门服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public List<ServeAggregationSimpleResDTO> hotServeList(@RequestParam("regionId") Long regionId) {
        List<ServeAggregationSimpleResDTO> serveAggregationSimpleResDTOs = homeService.hotServeList(regionId);
        return serveAggregationSimpleResDTOs;
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", required = true, dataTypeClass = Long.class)
    })
    public ServeAggregationSimpleResDTO serveDetail(@PathVariable("id") Long regionId) {
        ServeAggregationSimpleResDTO serveAggregationSimpleResDTO = homeService.serveDetail(regionId);
        return serveAggregationSimpleResDTO;
    }
}
