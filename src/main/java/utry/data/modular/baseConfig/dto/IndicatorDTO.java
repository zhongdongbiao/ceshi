package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class IndicatorDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * 指标编码
	 */
	@ApiModelProperty("指标编码")
	private String indicatorCode;
	/**
	 * 指标名称
	 */
	@ApiModelProperty("指标名称")
	private String indicatorName;
	/**
	 * 指标值
	 */
	@ApiModelProperty("指标值")
	private String indicatorValue;
}
