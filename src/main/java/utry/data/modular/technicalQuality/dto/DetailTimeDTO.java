package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 技术品质首页查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class DetailTimeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 管理编号
	 */
	@ApiModelProperty("管理编号")
	private String manageNumber;
}
