package utry.data.modular.ccBoard.hotLineAgent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 热线服务单跟进流程信息实体类
 * @author: WangXinhao
 * @create: 2022-10-24 15:02
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotOrderFollowProcess {

    /**
     * 唯一主键
     */
    private Long hotOrderFollowProcessId;

    /**
     * 热线服务单号
     */
    private String hotlineNumber;

    /**
     * 最近服务坐席id
     */
    private String lastServiceAgentId;

    /**
     * 最近服务坐席姓名
     */
    private String lastServiceAgentName;

    /**
     * 最近服务时间
     */
    private LocalDateTime lastServiceTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
