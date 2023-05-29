package utry.data.modular.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author wj
 * @Date 2022/5/11 10:06
 */
@Data
public class TemplateResultDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "planId", value = "key")
    private String planId;

    @ApiModelProperty(name = "planName", value = "方案名称")
    private String planName;
}
