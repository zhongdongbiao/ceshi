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
public class UserCategoryQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@ApiModelProperty(name="用户id")
	private String accountId;
}
