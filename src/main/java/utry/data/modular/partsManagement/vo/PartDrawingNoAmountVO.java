package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import utry.data.util.ConditionUtil;

/**
 * @program: data
 * @description: 部品库存部品图号在库金额表格视图
 * @author: WangXinhao
 * @create: 2022-06-15 15:24
 **/

@Data
@Builder
@ApiModel(value = "部品库存部品图号在库金额表格视图")
public class PartDrawingNoAmountVO {

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "缺件订单量")
    private Long shortageOrderCount;

    @ApiModelProperty(value = "在库金额")
    private Double costAmount;

    @ApiModelProperty(value = "需求金额")
    private Double demandAmount;

    @ApiModelProperty(value = "安全在库金额")
    private Double safeCostAmount;

    @ApiModelProperty(value = "采购在途订单金额")
    private Double purchaseInTransitAmount;

    @ApiModelProperty(value = "服务店订单量")
    private Long serviceStoreOrderCount;

    @ApiModelProperty(value = "采购在途订单合计数量")
    private Long purchaseInTransitTotalCount;

    @ApiModelProperty(value = "采购在途订单异常数量")
    private Long purchaseInTransitAbnormalCount;

    @ApiModelProperty(value = "服务店订单异常数量")
    private Long serviceStoreOrderAbnormalCount;
}
