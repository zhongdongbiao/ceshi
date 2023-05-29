package utry.data.modular.partsManagement.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;

import javax.validation.constraints.NotNull;

/**
 * @program: data
 * @description: 担当工厂条件查询数据传输类
 * @author: WangXinhao
 * @create: 2022-06-11 09:41
 **/
@Data
@ApiModel(value = "担当工厂条件查询数据传输类")
public class FactoryBearAmountQueryDTO {

    @ApiModelProperty(value = "工厂名称")
    private ConditionUtil factoryName;

    @ApiModelProperty(value = "当前在库金额")
    private ConditionUtil amount;

    @NotNull(message = "userId不能为空")
    @ApiModelProperty(value = "担当唯一标识")
    private String userId;

    @ApiModelProperty(value = "担当姓名")
    private String userName;

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty("分页页数")
    private Long pageNum;

    @ApiModelProperty("分页大小")
    private Long pageSize;
}
