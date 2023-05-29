package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 工厂别在库金额视图
 * @author: WangXinhao
 * @create: 2022-06-09 18:30
 **/
@Data
@Builder
@ApiModel(value = "工厂别在库金额业务对象")
public class WarehouseAmountVO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "工厂在库金额")
    private Double total;

    @ApiModelProperty(value = "百分比")
    private Double percent;
}
