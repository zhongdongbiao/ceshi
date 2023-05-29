package utry.data.modular.partsManagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @program: data
 * @description: 每日安全在库金额表
 * @author: WangXinhao
 * @create: 2022-06-20 17:13
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "每日安全在库金额表")
public class DailySafeDepositAmount {

    @ApiModelProperty(value = "自增主键")
    private Integer id;

    @ApiModelProperty(value = "需求金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "所属日期")
    private LocalDate date;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
