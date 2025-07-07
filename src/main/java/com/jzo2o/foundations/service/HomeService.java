package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.response.*;

import java.util.List;

public interface HomeService {

    /**
     * 根据区域id获取服务图标信息
     *
     * @param regionId 区域id
     * @return 服务图标列表
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);

    List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId);

    List<ServeAggregationSimpleResDTO> hotServeList(Long regionId);

    ServeAggregationSimpleResDTO serveDetail(Long regionId);

    Serve getServeByServeId(Long regionId);

    ServeItem getServeItemByServeItemId(Long serveItemId);
}
