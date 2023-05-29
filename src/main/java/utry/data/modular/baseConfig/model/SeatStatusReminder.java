package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 坐席状态提醒实体类
 * @author ldk
 * @date 2022-04-07 13:23:25
 */
@Data
public class SeatStatusReminder implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一id")
	private String seatStatusId;
	@ApiModelProperty(value = "状态名称")
	private String statusName;
	@ApiModelProperty(value = "超时时间")
	private String timeout;
	@ApiModelProperty(value = "创建时间")
	private String creationTime;
	@ApiModelProperty(value = "更新时间")
	private String updateTime;


}
