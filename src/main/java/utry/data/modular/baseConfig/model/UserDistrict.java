package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户大区关联实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserDistrict implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * 用户id
	 */
	@ApiModelProperty("用户id")
	private String accountId;
	/**
	 * 大区id
	 */
	@ApiModelProperty("大区ids")
	private String districtId;
}
