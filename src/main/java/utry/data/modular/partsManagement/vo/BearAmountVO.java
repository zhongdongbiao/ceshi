package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @program: data
 * @description: 担当别在库金额视图
 * @author: WangXinhao
 * @create: 2022-06-10 14:17
 **/
@Data
@Builder
@ApiModel(value = "担当别在库金额视图")
public class BearAmountVO {

    @ApiModelProperty(value = "担当唯一标识")
    private String userId;

    @ApiModelProperty(value = "担当姓名")
    private String userName;

    @ApiModelProperty(value = "在库金额")
    private Double total;
}
