package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 队列-目标关系实体类
 * @author ldk
 */
@Data
public class QueueTargetRelation implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty("queueTargetId")
	private String queueTargetId;
	@ApiModelProperty("targetName")
	private String queueId;
	@ApiModelProperty("creationTime")
	private String creationTime;
	@ApiModelProperty("updateTime")
	private String updateTime;

}
