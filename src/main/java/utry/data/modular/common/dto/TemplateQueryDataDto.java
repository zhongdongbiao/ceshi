package utry.data.modular.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Description
 * @Author wj
 * @Date 2022/5/11 10:06
 */
@Data
public class TemplateQueryDataDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "planId", value = "key")
    @NotEmpty(message = "方案Id不能为空")
    private String planId;

    @ApiModelProperty(name = "planName", value = "方案名称")
    @NotEmpty(message = "方案名称不能为空")
    private String planName;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    @NotEmpty(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    @NotEmpty(message = "结束时间不能为空")
    private String endTime;
}
