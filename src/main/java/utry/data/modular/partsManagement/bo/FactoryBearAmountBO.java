package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 工厂担当在库金额业务类
 * @author: WangXinhao
 * @create: 2022-06-11 13:51
 **/

@Data
@Builder
@ApiModel(value = "工厂担当在库金额业务类")
public class FactoryBearAmountBO {

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "在库金额")
    private BigDecimal total;
}
