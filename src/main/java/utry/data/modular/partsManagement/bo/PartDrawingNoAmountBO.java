package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 部件图号-在库金额业务类
 * @author: WangXinhao
 * @create: 2022-06-15 14:58
 **/

@Data
@ApiModel(value = "部件图号-在库金额业务类")
public class PartDrawingNoAmountBO {

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "在库金额")
    private BigDecimal amount;
}
