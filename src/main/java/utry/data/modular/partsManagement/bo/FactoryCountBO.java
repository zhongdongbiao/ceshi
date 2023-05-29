package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 工厂别数量业务类
 * @author: WangXinhao
 * @create: 2022-06-14 14:42
 **/
@Data
@ApiModel(value = "工厂别数量业务类")
public class FactoryCountBO {

    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "实际数量")
    private Long realityNumber;
}
