package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 导出查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ExportRepairRateByMonthDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 年月
	 */
	@ApiModelProperty("年月")
	private String time;
	/**
	 * 全年一次性修复率
	 */
	@ApiModelProperty("全年一次性修复率")
	private String repairRate;
}
