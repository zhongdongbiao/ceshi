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
public class TypeChildrenDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 类型code
	 */
	@ApiModelProperty("类型code")
	private String productTypeCode;
	/**
	 * 类型
	 */
	@ApiModelProperty("类型")
	private String name;
}
