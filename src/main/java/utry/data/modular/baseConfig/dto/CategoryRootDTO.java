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
public class CategoryRootDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * 品类code
	 */
	@ApiModelProperty("品类code")
	private String productCategoryCode;
	/**
	 * 品类
	 */
	@ApiModelProperty("品类")
	private String name;
	/**
	 * 子类集合
	 */
	@ApiModelProperty("子类集合")
	private List<TypeChildrenDTO> children;
}
