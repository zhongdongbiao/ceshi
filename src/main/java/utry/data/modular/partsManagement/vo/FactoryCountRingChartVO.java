package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 工厂别在库数量环形图视图
 * @author: WangXinhao
 * @create: 2022-06-14 14:35
 **/
@Data
@Builder
@ApiModel(value = "工厂别在库数量环形图视图")
public class FactoryCountRingChartVO {

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "在库数量")
    private Long count;

    @ApiModelProperty(value = "百分比")
    private String percent;
}
