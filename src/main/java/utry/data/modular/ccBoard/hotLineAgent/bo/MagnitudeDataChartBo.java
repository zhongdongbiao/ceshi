package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 量级数据图表业务类
 * @author: WangXinhao
 * @create: 2022-11-02 17:15
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagnitudeDataChartBo {

    /**
     * 时间 yyyy-MM-dd HH:mm:ss
     */
    private String time;

    /**
     * 坐席接起数量
     */
    private Integer connectNumber;

    /**
     * 接入量/进线量
     */
    private Integer accessNumber;

    /**
     * 队列10秒接通率
     */
    private String tenSecondRate;

    /**
     * 一线人力
     */
    private Double frontLineManpower;

    /**
     * 理论接起量
     */
    private Double theoryConnectNumber;
}
