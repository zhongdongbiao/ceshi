package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ProductInformationDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 产品型号
	 */
	@ApiModelProperty("产品型号")
	private String productModel;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private String productType;
	/**
	 * 上市日期
	 */
	@ApiModelProperty("上市日期")
	private String listingDate;
	/**
	 * 新品说明书上传日期
	 */
	@ApiModelProperty("新品说明书上传日期")
	private String manualTime;
	/**
	 * 新品维修手册上传日期
	 */
	@ApiModelProperty("新品维修手册上传日期")
	private String serviceManualTime;
	/**
	 * 更新时间
	 */
	@ApiModelProperty("更新时间")
	private String updateTime;
}
