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
public class FaultCauseDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 故障原因
	 */
	@ApiModelProperty("故障原因")
	private String faultCause;
	/**
	 * 故障原因代码
	 */
	@ApiModelProperty("故障原因代码")
	private String faultCauseCode;
	/**
	 * 故障现象个数
	 */
	@ApiModelProperty("故障现象个数")
	private String faultCodeNum;
}
