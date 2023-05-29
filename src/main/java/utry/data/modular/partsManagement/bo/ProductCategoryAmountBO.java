package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 品类别在库金额业务对象
 * @author: WangXinhao
 * @create: 2022-06-10 10:58
 **/
@Data
@ApiModel(value = "品类别在库金额业务对象")
public class ProductCategoryAmountBO {

    @ApiModelProperty(value = "品类代码")
    private String productCategoryCode;

    @ApiModelProperty(value = "品类名称")
    private String productCategory;

    @ApiModelProperty(value = "工厂在库金额")
    private BigDecimal total;
}
