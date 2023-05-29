package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 部件图号-在库数量业务类
 * @author: WangXinhao
 * @create: 2022-06-16 09:43
 **/

@Data
@ApiModel(value = "部件图号-在库数量业务类")
public class PartDrawingNoCountBO {

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "图号描述")
    private String describedDrawingNo;

    @ApiModelProperty(value = "在库数量")
    private Long partDrawingCount;
}
