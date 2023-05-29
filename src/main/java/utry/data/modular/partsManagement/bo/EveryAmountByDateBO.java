package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 每日的部品在库金额
 * @author: WangXinhao
 * @create: 2022-06-16 19:13
 **/
@Data
@ApiModel(value = "每日的部品在库金额")
public class EveryAmountByDateBO {

    @ApiModelProperty(value = "时间")
    private String inventoryDate;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
