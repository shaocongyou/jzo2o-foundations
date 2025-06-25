package com.jzo2o.foundations.service;

import cn.hutool.core.lang.Assert;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class ServeMapperTest {

    @Resource
    ServeMapper serveMapper;

    @Test
    void test_queryServeListByRegionId(){
        List<ServeResDTO> serveResDTOList = serveMapper.queryServeListByRegionId(1686303222843662337L);
        Assert.notEmpty(serveResDTOList, "查询服务列表为空");
    }
}
