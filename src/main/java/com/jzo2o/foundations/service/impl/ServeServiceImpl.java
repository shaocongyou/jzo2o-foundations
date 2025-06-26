package com.jzo2o.foundations.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2025-06-23
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

    @Resource
    private ServeItemMapper serveItemMapper;

    @Resource
    private RegionMapper regionMapper;

    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        return PageHelperUtils.selectPage(servePageQueryReqDTO,
                () -> baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
    }

    @Override
    public void batchadd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        // 遍历列表serveUpsertReqDTOList
        for (ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            // 对于每个未验证的serveUpsertReqDTO进行合法性校验
            // 1.服务项是否存在
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if (ObjectUtils.isNull(serveItem) || serveItem.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()) {
                throw new ForbiddenOperationException("服务项未启用或服务项不存在");
            }
            // 2.服务项是否在当前区域存在
            Integer count = lambdaQuery()
                    .eq(Serve::getRegionId,serveUpsertReqDTO.getRegionId())
                    .eq(Serve::getServeItemId,serveUpsertReqDTO.getServeItemId())
                    .count();
            if (count > 0) {
                throw new ForbiddenOperationException( serveItem.getName()+ "服务项已存在");
            }
            // 3.新增服务
            Serve serve = BeanUtils.toBean(serveUpsertReqDTO, Serve.class);
            Region region = regionMapper.selectById(serveUpsertReqDTO.getRegionId());
            serve.setCityCode(region.getCityCode());
//            System.out.println(serve.toString());
            baseMapper.insert(serve);
        }
    }

    @Override
    @Transactional
    public Serve updatePrice(Long id, BigDecimal price) {
        //1.更新服务价格
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getPrice, price)
                .update();
        if(!update){
            throw new CommonException("修改服务价格失败");
        }
        return baseMapper.selectById(id);
    }

    /**
     * @Description: 上架服务
     * @param id 服务 serve 的 id
     * @return
     */
    @Override
    @Transactional
    public Serve onSale(Long id) {
        Serve serve = baseMapper.selectById(id);
        if(ObjectUtils.isNull(serve)){
            throw new CommonException("服务不存在");
        }
        if(!(serve.getSaleStatus() == FoundationStatusEnum.INIT.getStatus() || serve.getSaleStatus() == FoundationStatusEnum.DISABLE.getStatus())){
            throw new CommonException("仅当服务处于 草稿 或 禁用 状态，可以上架，当前状态不满足");
        }
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if(ObjectUtils.isNull(serveItem)){
            throw new CommonException("服务项不存在");
        }
        if(serveItem.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()){
            throw new CommonException("服务项未启用");
        }
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus())
                .update();
        if(!update){
            throw new CommonException("上架服务失败");
        }
        return  baseMapper.selectById(id);
    }

}
