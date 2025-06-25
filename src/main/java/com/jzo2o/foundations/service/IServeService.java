package com.jzo2o.foundations.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务类
 * </p>
 *
 * @author itcast
 * @since 2025-06-23
 */
public interface IServeService extends IService<Serve> {

    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    void batchadd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    Serve updatePrice(Long id, BigDecimal price);
}
