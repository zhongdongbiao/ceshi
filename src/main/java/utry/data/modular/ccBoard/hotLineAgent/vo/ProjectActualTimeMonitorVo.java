package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目实时监控视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:00
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectActualTimeMonitorVo {

    /**
     * 接入量
     */
    private Integer accessNumber;

    /**
     * 接通率
     */
    private String connectionRate;

    /**
     * 10s率
     */
    private String tenSecondRate;

    /**
     * 10s率目标
     */
    private String tenSecondRateTarget;

    /**
     * 当前平均排队时间
     */
    private Integer avgQueueDuration;

    /**
     * 当日平均通话时长
     */
    private Integer avgCallInDuration;

    /**
     * 接入客户数
     */
    private Integer callInCustomerNumber;

    /**
     * 接起客户数
     */
    private Integer connectCustomerNumber;
}
