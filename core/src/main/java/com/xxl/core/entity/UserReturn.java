package com.xxl.core.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户还款表
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserReturn对象", description="用户还款表")
public class UserReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户商品名称")
    private String agentGoodsName;

    @ApiModelProperty(value = "批次号")
    private String agentBatchNo;

    @ApiModelProperty(value = "还款人绑定协议号")
    private String fromBindCode;

    @ApiModelProperty(value = "还款总额")
    private BigDecimal totalAmt;

    @ApiModelProperty(value = "P2P商户手续费")
    private BigDecimal voteFeeAmt;

    @ApiModelProperty(value = "还款明细数据")
    private String data;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除(1:已删除，0:未删除)")
    @TableLogic
    private Integer isDeleted;


}
