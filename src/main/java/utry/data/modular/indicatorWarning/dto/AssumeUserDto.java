package utry.data.modular.indicatorWarning.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/23 13:50
 * description 担当用户的信息dto
 */
@Data
public class AssumeUserDto {

    @ApiModelProperty(name = "userId", value = "用户id")
    private String userId;

    @ApiModelProperty(name = "account",  value = "账号")
    private String account;

    @ApiModelProperty(name = "realName", value = "真实姓名")
    private String realName;

    @ApiModelProperty(name = "indicatorValue", value = "指标值")
    private String indicatorValue;

}
