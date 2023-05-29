package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.Data;

/**
 * @program: data
 * @description: 时段通话时长、振铃时长、单位小时接入量/呼出量
 * @author: WangXinhao
 * @create: 2022-11-04 14:39
 **/

@Data
public class IntervalCallDurationRingTimeHourlyCallNumberBo {

    /**
     * 时间节点
     */
    private String time;

    /**
     * 通话时长
     */
    private Integer callDuration;

    /**
     * 振铃时长
     */
    private Integer ringTime;

    /**
     * 单位小时接入量/呼出量
     */
    private Integer hourlyCallNumber;
}
