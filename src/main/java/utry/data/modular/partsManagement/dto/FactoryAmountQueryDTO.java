package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;

/**
 * @program: data
 * @description: 部品库存工厂在库金额表格查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-06-13 10:58
 **/
@Data
@ApiModel(value = "部品库存工厂在库金额表格查询条件数据传输类")
public class FactoryAmountQueryDTO {

    @ApiModelProperty(value = "日期时间")
    private String dateTime;

    @ApiModelProperty(value = "工厂名称")
    private ConditionUtil factoryName;

    @ApiModelProperty(value = "担当姓名")
    private ConditionUtil userName;

    @ApiModelProperty(value = "在库金额")
    private ConditionUtil costAmount;

    @ApiModelProperty(value = "需求金额")
    private ConditionUtil demandAmount;

    @ApiModelProperty(value = "安全在库金额")
    private ConditionUtil safeCostAmount;

    @ApiModelProperty(value = "采购在途订单金额")
    private ConditionUtil purchaseInTransitAmount;

    @ApiModelProperty("分页页数")
    private Long pageNum;

    @ApiModelProperty("分页大小")
    private Long pageSize;
}
