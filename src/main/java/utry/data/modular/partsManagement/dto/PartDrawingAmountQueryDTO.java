package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;

/**
 * @program: data
 * @description: 部品库存部品图号在库金额表格查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-06-15 14:08
 **/
@Data
@ApiModel(value = "部品库存部品图号在库金额表格查询条件数据传输类")
public class PartDrawingAmountQueryDTO {

    @ApiModelProperty(value = "日期时间")
    private String dateTime;

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "部件图号")
    private ConditionUtil partDrawingNo;

    @ApiModelProperty(value = "缺件订单量")
    private ConditionUtil shortageOrderCount;

    @ApiModelProperty(value = "在库金额")
    private ConditionUtil costAmount;

    @ApiModelProperty(value = "需求金额")
    private ConditionUtil demandAmount;

    @ApiModelProperty(value = "安全在库金额")
    private ConditionUtil safeCostAmount;

    @ApiModelProperty(value = "采购在途订单金额")
    private ConditionUtil purchaseInTransitAmount;

    @ApiModelProperty(value = "服务店订单量")
    private ConditionUtil serviceStoreOrderCount;

    @ApiModelProperty("分页页数")
    private Long pageNum;

    @ApiModelProperty("分页大小")
    private Long pageSize;
}
