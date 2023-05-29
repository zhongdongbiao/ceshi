package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 服务店异常订单业务类
 * @author: WangXinhao
 * @create: 2022-06-16 13:57
 **/

@Data
@ApiModel(value = "服务店异常订单业务类")
public class ServiceStoreOrderAbnormalCountBO {

    @ApiModelProperty(value = "单据号")
    private String documentNumber;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "数量")
    private Long count;
}
