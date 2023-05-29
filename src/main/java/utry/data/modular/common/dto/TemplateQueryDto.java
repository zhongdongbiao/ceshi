package utry.data.modular.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wj
 * @Date 2022/5/11 10:06
 */
@Data
public class TemplateQueryDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "phoneNum", value = "电话号码")
    private String phoneNum;

    @ApiModelProperty(name = "businessCode", value = "业务编码")
    private String businessCode;
}
