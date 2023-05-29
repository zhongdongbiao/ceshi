package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class CcCoreTarget implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "核心目标唯一id")
	private String ccCoreTargetId;
	@ApiModelProperty(value = "目标名称")
	private String targetName;
	@ApiModelProperty(value = "目标月")
	private String targetMonth;
	@ApiModelProperty(value = "10s接通率")
	private String connRate;
	@ApiModelProperty(value = "创建时间")
	private String creationTime;
	@ApiModelProperty(value = "更新时间")
	private String updateTime;
	@ApiModelProperty(value = "页码")
	private String pageSize;
	@ApiModelProperty(value = "当前页")
	private String currentPage;

}
