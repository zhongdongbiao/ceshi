package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订用户工厂关联DTO
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserTypeDTO implements Serializable {
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
	 * 旧用户id
	 */
	@ApiModelProperty("旧用户id")
	private String oldAccountId;
	/**
	 * 类型ids
	 */
	@ApiModelProperty("类型ids")
	private List<String> list;
	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private String createTime;
}
