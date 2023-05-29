package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 安全在库金额业务对象
 * @author: WangXinhao
 * @create: 2022-06-09 18:23
 **/
@Data
@ApiModel(value = "采购订单订单量")
public class PurchaseOrderBO {

    @ApiModelProperty(value = "仓库名称")
    private String warehouseCode;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "总数量")
    private Integer total;

    @ApiModelProperty(value = "异常数量")
    private Integer abnormal;
}
