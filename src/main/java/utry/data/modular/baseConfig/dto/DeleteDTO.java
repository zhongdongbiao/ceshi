package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 品类类型树
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class DeleteDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * targetId
	 */
	@ApiModelProperty("targetId")
	private String targetId;
	/**
	 * accountId
	 */
	@ApiModelProperty("accountId")
	private String accountId;
}
