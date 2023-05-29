package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;

/**
 * @program: data
 * @description: 部品库存工厂在库数量表格查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-06-15 11:12
 **/
@Data
@ApiModel(value = "部品库存工厂在库数量表格查询条件数据传输类")
public class FactoryCountQueryDTO {

    @ApiModelProperty(value = "日期时间")
    private String dateTime;

    @ApiModelProperty(value = "工厂名称")
    private ConditionUtil factoryName;

    @ApiModelProperty(value = "担当姓名")
    private ConditionUtil userName;

    @ApiModelProperty(value = "在库数量")
    private ConditionUtil costCount;

    @ApiModelProperty(value = "需求数量")
    private ConditionUtil demandCount;

    @ApiModelProperty(value = "安全在库数量")
    private ConditionUtil safeCostCount;

    @ApiModelProperty(value = "采购在途订单数量")
    private ConditionUtil purchaseInTransitCount;

    @ApiModelProperty("分页页数")
    private Long pageNum;

    @ApiModelProperty("分页大小")
    private Long pageSize;
}
