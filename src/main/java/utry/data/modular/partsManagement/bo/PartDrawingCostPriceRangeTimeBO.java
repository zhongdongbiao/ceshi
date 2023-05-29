package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 仓库-部件图号-单价-时间业务类
 * @author: WangXinhao
 * @create: 2022-06-16 20:08
 **/
@Data
@ApiModel(value = "仓库-部件图号-单价-时间业务类")
public class PartDrawingCostPriceRangeTimeBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "成本单价")
    private BigDecimal costPrice;
}
