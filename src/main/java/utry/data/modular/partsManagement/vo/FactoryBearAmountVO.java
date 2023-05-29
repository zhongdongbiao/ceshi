package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @program: data
 * @description: 工厂担当在库金额视图
 * @author: WangXinhao
 * @create: 2022-06-10 17:26
 **/

@Data
@Builder
@ApiModel(value = "工厂担当在库金额视图")
public class FactoryBearAmountVO {

    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "担当姓名")
    private String userName;

    @ApiModelProperty(value = "在库金额")
    private Double amount;
}
