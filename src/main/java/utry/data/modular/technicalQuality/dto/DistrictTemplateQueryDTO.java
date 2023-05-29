package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 大区模板查询
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class DistrictTemplateQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 编码
	 */
	@ApiModelProperty("编码")
	private String code;
	/**
	 * 模板名称
	 */
	@ApiModelProperty("模板名称")
	private String name;
}
