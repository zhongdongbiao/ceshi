package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 担当别在库金额业务类
 * @author: WangXinhao
 * @create: 2022-06-10 14:29
 **/
@Data
@ApiModel(value = "担当别在库金额业务类")
public class BearAmountBO {

    @ApiModelProperty(value = "担当唯一标识")
    private String userId;

    @ApiModelProperty(value = "在库金额")
    private BigDecimal total;
}
