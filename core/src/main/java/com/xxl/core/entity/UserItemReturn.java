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
 * 用户还款明细表
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserItemReturn对象", description="用户还款明细表")
public class UserItemReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "还款id")
    private Long userReturnId;

    @ApiModelProperty(value = "还款项目编号")
    private String agentProjectCode;

    @ApiModelProperty(value = "投资单号")
    private String voteBillNo;

    @ApiModelProperty(value = "收款人（投资人）")
    private String toBindCode;

    @ApiModelProperty(value = "还款金额")
    private BigDecimal transitAmt;

    @ApiModelProperty(value = "还款本金")
    private BigDecimal baseAmt;

    @ApiModelProperty(value = "还款利息")
    private BigDecimal benifitAmt;

    @ApiModelProperty(value = "商户手续费")
    private BigDecimal feeAmt;

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
