package com.jzo2o.foundations.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeItemService;
import com.jzo2o.foundations.service.IServeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private IRegionService regionService;

    @Resource
    private ServeMapper serveMapper;

    @Resource
    private IServeService serveService;

    @Resource
    private IServeItemService serveItemService;

    @Resource
    private HomeService homeService;
    /**
     * 根据区域id查询已开通的服务类型
     *
     * @param regionId 区域id
     * @return 已开通的服务类型
     */
    @Override
    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {
        //1.校验当前城市是否为启用状态
        Region region = regionService.getById(regionId);
        if (ObjectUtil.isEmpty(region) || ObjectUtil.equal(FoundationStatusEnum.DISABLE.getStatus(), region.getActiveStatus())) {
            return Collections.emptyList();
        }

        //2.根据城市编码查询所有的服务图标
        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        //3.服务类型取前两个，每个类型下服务项取前4个
        //list的截止下标
        int endIndex = list.size() >= 2 ? 2 : list.size();
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(list.subList(0, endIndex));
        serveCategoryResDTOS.forEach(v -> {
            List<ServeSimpleResDTO> serveResDTOList = v.getServeResDTOList();
            //serveResDTOList的截止下标
            int endIndex2 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex2));
            v.setServeResDTOList(serveSimpleResDTOS);
        });

        return serveCategoryResDTOS;
    }

    @Override
    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId) {
        //1.校验当前城市是否为启用状态
        Region region = regionService.getById(regionId);
        if (ObjectUtil.isEmpty(region) || ObjectUtil.equal(FoundationStatusEnum.DISABLE.getStatus(), region.getActiveStatus())) {
            return Collections.emptyList();
        }

        //2.获取当前区域的服务类型
        List<ServeAggregationTypeSimpleResDTO> serveAggregationTypeSimpleResDTOList = serveMapper.serveTypeList(regionId);
        if(ObjectUtils.isEmpty(serveAggregationTypeSimpleResDTOList)){
            return Collections.emptyList();
        }
        System.out.println("serveTypeList 缓存未命中");
        return serveAggregationTypeSimpleResDTOList;
    }

    @Override
    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public List<ServeAggregationSimpleResDTO> hotServeList(Long regionId) {
        //1.校验当前城市是否为启用状态
        Region region = regionService.getById(regionId);
        if (ObjectUtil.isEmpty(region) || ObjectUtil.equal(FoundationStatusEnum.DISABLE.getStatus(), region.getActiveStatus())) {
            return Collections.emptyList();
        }

        //2.获取当前区域的服务类型
        List<ServeAggregationSimpleResDTO> serveAggregationSimpleResDTOs = serveMapper.hotServeList(regionId);
        if(ObjectUtils.isEmpty(serveAggregationSimpleResDTOs)){
            return Collections.emptyList();
        }
        System.out.println("hotServeList 缓存未命中");
        return serveAggregationSimpleResDTOs;
    }

    @Override
    public ServeAggregationSimpleResDTO serveDetail(Long regionId) {
//        Serve serve = serveService.getById(regionId);
        Serve serve = homeService.getServeByServeId(regionId);
        Long serveItemId = serve.getServeItemId();
//        ServeItem serveItem = serveItemService.getById(serveItemId);
        ServeItem serveItem = homeService.getServeItemByServeItemId(serveItemId);

        ServeAggregationSimpleResDTO serveAggregationSimpleResDTO = new ServeAggregationSimpleResDTO();
        serveAggregationSimpleResDTO.setCityCode(serve.getCityCode());
        serveAggregationSimpleResDTO.setServeItemName(serveItem.getName());
        serveAggregationSimpleResDTO.setServeItemId(serveItem.getId());
        serveAggregationSimpleResDTO.setUnit(serveItem.getUnit());
        serveAggregationSimpleResDTO.setDetailImg(serveItem.getDetailImg());
        serveAggregationSimpleResDTO.setPrice(serve.getPrice());
        serveAggregationSimpleResDTO.setServeItemImg(serveItem.getImg());

        if(ObjectUtils.isEmpty(serveAggregationSimpleResDTO)){
            return null;
        }

        return serveAggregationSimpleResDTO;
    }

    // 如果为空，则缓存穿透，保留半小时，unless的条件就是result != null
    // 如果不为空，永久保存，unless的条件就是result == null
    @Override
    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.SERVE, key = "#regionId", unless = "#result != null", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    @Cacheable(value = RedisConstants.CacheName.SERVE, key = "#regionId", unless = "#result == null", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public Serve getServeByServeId(Long regionId) {
        System.out.println("getServeByServeId 缓存未命中");
        return serveService.getById(regionId);
    }

    @Override
    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ITEM, key = "#serveItemId", unless = "#result != null", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ITEM, key = "#serveItemId", unless = "#result == null", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public ServeItem getServeItemByServeItemId(Long serveItemId) {
        System.out.println("getServeItemByServeItemId 缓存未命中");
        return serveItemService.getById(serveItemId);
    }
}
