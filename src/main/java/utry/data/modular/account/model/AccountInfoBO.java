package utry.data.modular.account.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: data
 * @description: hrm用户业务类
 * @author: WangXinhao
 * @create: 2022-06-10 16:46
 **/
@Data
@ApiModel(value = "hrm用户业务类")
public class AccountInfoBO {

    @ApiModelProperty(value = "用户唯一标识")
    private String accountId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;
}
