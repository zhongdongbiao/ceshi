package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 故障分析饼图实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class PieChartDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 部品图号
	 */
	@ApiModelProperty("部品图号")
	private String partDrawingNo;
	/**
	 * 图号描述
	 */
	@ApiModelProperty("图号描述")
	private String describedDrawingNo;
	/**
	 * 零件更换数
	 */
	@ApiModelProperty("零件更换数")
	private String replaceNum;
	/**
	 * 零件调整数
	 */
	@ApiModelProperty("零件调整数")
	private String repairNum;
}
