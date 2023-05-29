package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 服务点备货订单数量业务类
 * @author: WangXinhao
 * @create: 2022-06-15 19:09
 **/

@Data
@ApiModel(value = "服务点备货订单数量业务类")
public class ServiceStoreOrderCountBO {

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "数量")
    private Long count;
}
