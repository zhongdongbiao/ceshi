package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

/**
 * @program: data
 * @description: 量级数据图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:40
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagnitudeDataChartVo {

    /**
     * 时间 HH:mm
     */
    private String time;

    /**
     * 理论接起量
     */
    private ChartPointVo theoryConnectNumber;

    /**
     * 坐席接起数量
     */
    private ChartPointVo connectNumber;

    /**
     * 接入量/进线量
     */
    private ChartPointVo accessNumber;

    /**
     * 一线人力
     */
    private ChartPointVo frontLineManpower;

    /**
     * 队列10秒接通率
     */
    private ChartPointVo tenSecondRate;
}
