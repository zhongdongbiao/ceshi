package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 仓库-部件图号-数量-时间业务类
 * @author: WangXinhao
 * @create: 2022-06-16 20:01
 **/
@Data
@ApiModel(value = "仓库-部件图号-数量-时间业务类")
public class PartDrawingNoNeedNumberOrderStartTimeBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "需要数量")
    private Long needNumber;

    @ApiModelProperty(value = "时间")
    private String orderStartTime;
}
