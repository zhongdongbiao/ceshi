package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 品类别在库金额视图
 * @author: WangXinhao
 * @create: 2022-06-10 11:03
 **/
@Data
@Builder
@ApiModel(value = "品类别在库金额视图")
public class ProductCategoryAmountVO {

    @ApiModelProperty(value = "品类代码")
    private String productCategoryCode;

    @ApiModelProperty(value = "品类名称")
    private String productCategory;

    @ApiModelProperty(value = "工厂在库金额")
    private Double total;

    @ApiModelProperty(value = "百分比")
    private String percent;
}
