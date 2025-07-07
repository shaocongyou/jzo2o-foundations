package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * springCache缓存同步任务
 *
 * @author itcast
 * @create 2023/8/15 18:14
 **/
@Slf4j
@Component
public class SpringCacheSyncHandler {

    @Resource
    private IRegionService regionService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private HomeService homeService;

    /**
     * 已启用区域缓存更新
     * 每日凌晨1点执行
     */
    @XxlJob("activeRegionCacheSync")
    public void activeRegionCacheSync() throws Exception {
        log.info(">>>>>>>>开始进行缓存同步，更新已启用区域");

        //删除缓存
        Boolean delete = redisTemplate.delete(RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS");

        //通过查询开通区域列表进行缓存
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();

        //遍历区域对该区域下的服务类型进行缓存
        regionSimpleResDTOS.forEach(item->{
            //区域id
            Long regionId = item.getId();

            //删除该区域下的首页服务列表
            String serve_type_key = RedisConstants.CacheName.SERVE_ICON + "::" + regionId;
            redisTemplate.delete(serve_type_key);
            homeService.queryServeIconCategoryByRegionIdCache(regionId);
            //todo 删除该区域下的服务类型列表缓存
        });
    }



    /**
     * 用户端首页所选城市服务缓存更新
     * 每日凌晨1点执行
     */
    @XxlJob(value = "cityServeCacheSync")
    public void cityServeCacheSync() {
        log.info(">>>>>>>>开始进行缓存同步，更新用户端首页所选城市服务");
        //1.清理所有城市服务缓存
        Set serveIconCacheKeys = redisTemplate.keys(RedisConstants.CacheName.SERVE_ICON.concat("*"));
        Set hotServeCacheKeys = redisTemplate.keys(RedisConstants.CacheName.HOT_SERVE.concat("*"));
        Set serveTypeCacheKeys = redisTemplate.keys(RedisConstants.CacheName.SERVE_TYPE.concat("*"));
        Set cacheKeys = new HashSet<>();
        cacheKeys.addAll(serveIconCacheKeys);
        cacheKeys.addAll(hotServeCacheKeys);
        cacheKeys.addAll(serveTypeCacheKeys);
        redisTemplate.delete(cacheKeys);

        //2.获取所有已启用的区域，提取区域id
        List<RegionSimpleResDTO> regionSimpleResDTOList = regionService.queryActiveRegionList();
        List<Long> activeRegionIdList = regionSimpleResDTOList.stream().map(RegionSimpleResDTO::getId).collect(Collectors.toList());

        //3.循环对每个已启用城市相关服务缓存更新
        for (Long regionId : activeRegionIdList) {
            homeService.queryServeIconCategoryByRegionIdCache(regionId);
            homeService.hotServeList(regionId);
            homeService.serveTypeList(regionId);
        }

        log.info(">>>>>>>>更新用户端首页所选城市服务完成");
    }

}