package com.jzo2o.foundations.model.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("首页热门服务")
public class ServeHotResDTO {

    /**
     * 城市编码
     */
    @ApiModelProperty("城市编码")
    private String cityCode;

    /**
     * 服务项名称
     */
    @ApiModelProperty("服务项名称")
    private String serveItemName;

    /**
     * 服务项id
     */
    @ApiModelProperty("服务项id")
    private Long serveItemId;

    /**
     * 服务单位
     */
    @ApiModelProperty("服务单位")
    private Integer unit;

    /**
     * 服务详图
     */
    @ApiModelProperty("服务详图")
    private String detalImg;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    private Number price;

    /**
     * 服务项图片
     */
    @ApiModelProperty("服务项图片")
    private String serveItemImg;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;
}
