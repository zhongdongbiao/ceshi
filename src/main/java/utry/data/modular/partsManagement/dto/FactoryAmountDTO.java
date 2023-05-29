package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: 工厂别在库金额数据传输类
 * @author: WangXinhao
 * @create: 2022-06-21 15:30
 **/
@Data
@ApiModel(value = "工厂别在库金额数据传输类")
public class FactoryAmountDTO {

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;
}
