package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 指标实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class IndicatorConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * 指标担当关联id
	 */
	@ApiModelProperty("指标担当关联id")
	private String indicatorUserId;
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
	/**
	 * 目标id
	 */
	@ApiModelProperty("目标id")
	private String targetId;
}
