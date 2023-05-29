package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 人力资源系数实体类
 * @author ldk
 * @date 2022-04-07 13:23:25
 */
@Data
public class HumanResCoef implements Serializable {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "工号")
	private String jobNo;
	@ApiModelProperty(value = "人力资源系数")
	private Double manPowerCoef;
	@ApiModelProperty(value = "坐席编号")
	private String seats;
	@ApiModelProperty(value = "唯一id")
	private String humanResCoefId;
	@ApiModelProperty(value = "创建时间")
	private String creationTime;
	@ApiModelProperty(value = "更新时间")
	private String updateTime;
	@ApiModelProperty(value = "每页数量")
	private String pageSize;
	@ApiModelProperty(value = "当前页")
	private String currentPage;

}
