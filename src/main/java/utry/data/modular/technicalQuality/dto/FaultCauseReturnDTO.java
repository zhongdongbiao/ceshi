package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 故障分析实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class FaultCauseReturnDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 故障列表
	 */
	@ApiModelProperty("故障列表")
	private List<FaultCauseDTO> faultCauseDTOS;
	/**
	 * 部品列表
	 */
	@ApiModelProperty("部品列表")
	private List<PieChartDTO> pieChartDTOS;
}
