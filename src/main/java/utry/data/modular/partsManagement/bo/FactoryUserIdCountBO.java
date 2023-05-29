package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 工厂担当在库数量业务类
 * @author: WangXinhao
 * @create: 2022-06-15 11:17
 **/
@Data
@ApiModel(value = "工厂担当在库数量业务类")
public class FactoryUserIdCountBO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "用户唯一标识")
    private String userId;

    @ApiModelProperty(value = "工厂在库数量")
    private Long factoryCount;
}
