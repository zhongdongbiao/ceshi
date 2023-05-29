package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 部品库存工厂在库金额表格视图
 * @author: WangXinhao
 * @create: 2022-06-13 10:48
 **/

@Data
@Builder
@ApiModel(value = "部品库存工厂在库金额表格视图")
public class FactoryAmountVO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "担当姓名")
    private String userName;

    @ApiModelProperty(value = "在库金额")
    private Double costAmount;

    @ApiModelProperty(value = "需求金额")
    private Double demandAmount;

    @ApiModelProperty(value = "安全在库金额")
    private Double safeCostAmount;

    @ApiModelProperty(value = "采购在途订单金额")
    private Double purchaseInTransitAmount;

    @ApiModelProperty(value = "采购在途订单合计数量")
    private Long purchaseInTransitTotalCount;

    @ApiModelProperty(value = "采购在途订单异常数量")
    private Long purchaseInTransitAbnormalCount;

}
