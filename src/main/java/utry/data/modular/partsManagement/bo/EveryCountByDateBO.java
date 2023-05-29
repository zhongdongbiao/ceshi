package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description:
 * @author: WangXinhao
 * @create: 2022-06-17 19:32
 **/
@Data
public class EveryCountByDateBO {

    @ApiModelProperty(value = "时间")
    private String inventoryDate;

    @ApiModelProperty(value = "数量")
    private Long count;
}
