package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 部品库存部品图号在库数量表格视图
 * @author: WangXinhao
 * @create: 2022-06-16 09:47
 **/

@Data
@Builder
@ApiModel(value = "部品库存部品图号在库数量表格视图")
public class PartDrawingNoCountVO {

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "图号描述")
    private String describedDrawingNo;

    @ApiModelProperty(value = "缺件订单量")
    private Long shortageOrderCount;

    @ApiModelProperty(value = "在库数量")
    private Long costCount;

    @ApiModelProperty(value = "需求数量")
    private Long demandCount;

    @ApiModelProperty(value = "安全在库数量")
    private Long safeCostCount;

    @ApiModelProperty(value = "采购在途订单数量")
    private Long purchaseInTransitCount;

    @ApiModelProperty(value = "服务店订单量")
    private Long serviceStoreOrderCount;

    @ApiModelProperty(value = "采购在途订单异常数量")
    private Long purchaseInTransitAbnormalCount;

    @ApiModelProperty(value = "服务店订单异常数量")
    private Long serviceStoreOrderAbnormalCount;
}
