package utry.data.modular.partsManagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @program: data
 * @description: 部品库存表
 * @author: WangXinhao
 * @create: 2022-06-07 10:35
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "部品库存表")
public class PartDrawingStock implements Serializable {
    private static final long serialVersionUID = 4346038178558920131L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "库位编号")
    private String locationNumber;

    @ApiModelProperty(value = "图号描述")
    private String describedDrawingNo;

    @ApiModelProperty(value = "期初数量")
    private Integer openNumber;

    @ApiModelProperty(value = "当前接收")
    private Integer currentReception;

    @ApiModelProperty(value = "当前发放")
    private Integer currentProvide;

    @ApiModelProperty(value = "当前调整")
    private Integer currentAdjust;

    @ApiModelProperty(value = "实际数量")
    private Integer realityNumber;

    @ApiModelProperty(value = "分配数量")
    private Integer distributionNumber;

    @ApiModelProperty(value = "缺货数量")
    private Integer stockoutNumber;

    @ApiModelProperty(value = "成本单价")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "成本金额")
    private BigDecimal costAmount;

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "创建时间（从第三方获取到数据的时间）")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
