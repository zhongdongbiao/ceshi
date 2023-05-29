package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.modular.partsManagement.model.PartOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订用户工厂关联DTO
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserFactoryDTO implements Serializable {
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
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private String createTime;
	/**
	 * 工厂ids
	 */
	@ApiModelProperty("工厂ids")
	private List<String> list;

}
