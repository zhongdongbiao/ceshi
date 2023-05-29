package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 队列目标实体类
 * @author ldk
 */
@Data
public class QueueTarget implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "queueTargetId")
	private String queueTargetId;
	@ApiModelProperty("targetName")
	private String targetName;
	@ApiModelProperty("targetMonth")
	private String targetMonth;
	@ApiModelProperty("connRate")
	private String connRate;
	@ApiModelProperty("queues")
	private List<String> queues;
	@ApiModelProperty("creationTime")
	private String creationTime;
	@ApiModelProperty("updateTime")
	private String updateTime;
	@ApiModelProperty("pageSize")
	private String pageSize;
	@ApiModelProperty("currentPage")
	private String currentPage;

}
