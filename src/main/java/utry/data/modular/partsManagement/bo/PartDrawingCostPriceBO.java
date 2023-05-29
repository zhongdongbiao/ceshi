package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 库存成本单价业务类
 * @author: WangXinhao
 * @create: 2022-06-13 16:05
 **/
@Data
@ApiModel(value = "库存成本单价业务类")
public class PartDrawingCostPriceBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "成本单价")
    private BigDecimal costPrice;
}
