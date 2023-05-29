package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 工厂别在库金额环形图视图
 * @author: WangXinhao
 * @create: 2022-06-14 13:34
 **/
@Data
@Builder
@ApiModel(value = "工厂别在库金额环形图视图")
public class FactoryAmountRingChartVO {

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "在库金额")
    private Double amount;

    @ApiModelProperty(value = "百分比")
    private String percent;
}
