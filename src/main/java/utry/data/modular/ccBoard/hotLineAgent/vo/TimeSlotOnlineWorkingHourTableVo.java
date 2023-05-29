package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 人力数据图表-时段在线及工时表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:26
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotOnlineWorkingHourTableVo {

    /**
     * 姓名
     */
    private String agentName;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 接入量/进线量
     */
    private Integer accessNumber;

    /**
     * 单位小时接入量
     */
    private Integer hourlyAccessNumber;

    /**
     * 工时利用率
     */
    private String manHourUtilizationRate;

    /**
     * 全天工时标志
     */
    private List<TimeDurationVo> timeDuration;
}
