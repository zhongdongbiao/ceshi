package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 大区模板DTO
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class DistrictTemplateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 类型
	 */
	@ApiModelProperty("类型")
	private List<String> list;
	/**
	 * 品类
	 */
	@ApiModelProperty("品类")
	private List<String> productCategoryCode;
	/**
	 * 模板名称
	 */
	@ApiModelProperty("模板名称")
	private String name;
}
