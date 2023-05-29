package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import java.lang.Long;

/**
 * @program: data
 * @description: 部品库存工厂在库数量表格视图
 * @author: WangXinhao
 * @create: 2022-06-15 11:29
 **/

@Data
@Builder
@ApiModel(value = "部品库存工厂在库数量表格视图")
public class FactoryCountVO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "担当姓名")
    private String userName;

    @ApiModelProperty(value = "在库数量")
    private Long costCount;

    @ApiModelProperty(value = "需求数量")
    private Long demandCount;

    @ApiModelProperty(value = "安全在库数量")
    private Long safeCostCount;

    @ApiModelProperty(value = "采购在途订单数量")
    private Long purchaseInTransitCount;

    @ApiModelProperty(value = "采购在途订单合计数量")
    private Long purchaseInTransitTotalCount;

    @ApiModelProperty(value = "采购在途订单异常数量")
    private Long purchaseInTransitAbnormalCount;
}
