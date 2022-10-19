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
 * 用户投资表
 * </p>
 *
 * @author xxl
 * @since 2022-10-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserInvest对象", description="用户投资表")
public class UserInvest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "投资人绑定协议号")
    private String voteBindCode;

    @ApiModelProperty(value = "借款人绑定协议号")
    private String benefitBindCode;

    @ApiModelProperty(value = "项目编号")
    private String agentProjectCode;

    @ApiModelProperty(value = "项目名称")
    private String agentProjectName;

    @ApiModelProperty(value = "商户订单号")
    private String agentBillNo;

    @ApiModelProperty(value = "投资金额")
    private BigDecimal voteAmt;

    @ApiModelProperty(value = "投资奖励金额")
    private BigDecimal votePrizeAmt;

    @ApiModelProperty(value = "P2P商户手续费")
    private BigDecimal voteFeeAmt;

    @ApiModelProperty(value = "项目总金额")
    private BigDecimal projectAmt;

    @ApiModelProperty(value = "投资备注")
    private String note;

    @ApiModelProperty(value = "状态（0：默认 1：已放款 -1：已撤标）")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除(1:已删除，0:未删除)")
    @TableLogic
    private Integer isDeleted;


}
