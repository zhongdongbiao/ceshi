package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 部件图号需要数量业务类
 * @author: WangXinhao
 * @create: 2022-06-13 14:52
 **/
@Data
@ApiModel(value = "部件图号需要数量业务类")
public class PartDrawingNoNeedNumberBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "需要数量")
    private Long needNumber;
}
