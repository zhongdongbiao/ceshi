package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 采购在途订单数量业务类
 * @author: WangXinhao
 * @create: 2022-06-14 16:35
 **/
@Data
@ApiModel(value = "采购在途订单数量业务类")
public class PurchaseOrderCountBO {

    @ApiModelProperty(value = "仓库名称")
    private String warehouseCode;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "数量")
    private Long count;
}
