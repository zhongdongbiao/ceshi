package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 工厂别在库金额业务对象
 * @author: WangXinhao
 * @create: 2022-06-09 18:23
 **/
@Data
@ApiModel(value = "工厂别在库金额业务对象")
public class WarehouseAmountBO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "工厂在库金额")
    private BigDecimal total;
}
