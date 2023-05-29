package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: data
 * @description: 部品库存数据传输类
 * @author: WangXinhao
 * @create: 2022-06-06 15:17
 **/
@Data
@ApiModel(value = "部品库存数据传输类")
public class PartDrawingStockDTO implements Serializable {

    private static final long serialVersionUID = 5486793652722552813L;


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
    private String costPrice;

    @ApiModelProperty(value = "成本金额")
    private String costAmount;

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

}
