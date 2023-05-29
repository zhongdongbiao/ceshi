package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 一次性修复率折线图实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class LineChartDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 时间
	 */
	@ApiModelProperty("时间：月日")
	private String time;
	/**
	 * 一次性修复率
	 */
	@ApiModelProperty("一次性修复率")
	private String repairRate;
}
