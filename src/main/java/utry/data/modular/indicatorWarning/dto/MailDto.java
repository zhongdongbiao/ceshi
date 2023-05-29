package utry.data.modular.indicatorWarning.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 16:35
 * description 站内信的DTO
 */
@Data
public class MailDto {

    @ApiModelProperty(name = "complainNumber", value = "投诉单号")
    private String complaintNumber;

    @ApiModelProperty(name = "systemState", value = "系统状态")
    private String systemState;

    @ApiModelProperty(name = "complaintStartTime", value = "投诉开始时间")
    private String complaintStartTime;

    @ApiModelProperty(name = "complaintEndTime", value = "投诉结案时间")
    private String complaintEndTime;

}
