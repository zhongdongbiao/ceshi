package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.modular.technicalQuality.dto.ConditionDTO;

import java.io.Serializable;

/**
 * 品类类型树
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ModelTimeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 上市日期
	 */
	@ApiModelProperty("上市日期")
	private String time;
	/**
	 * 型号
	 */
	@ApiModelProperty("型号")
	private ConditionDTO productModel;
}
