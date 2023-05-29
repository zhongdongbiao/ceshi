package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 缺件订单量业务类
 * @author: WangXinhao
 * @create: 2022-06-15 18:18
 **/

@Data
@ApiModel(value = "缺件订单量业务类")
public class MissDealOrderBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "数量")
    private Long count;
}
