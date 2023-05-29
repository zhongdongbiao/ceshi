package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-热线服务单量视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:17
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderNumberVo {

    /**
     * 当日热线服务总量
     */
    private Integer dailyHotlineServiceNumber;

    /**
     * 当周服务总量
     */
    private Integer weeklyServiceNumber;

    /**
     * 当月服务总量
     */
    private Integer monthlyServiceNumber;

    /**
     * 当日服务投诉总量
     */
    private Integer dailyComplaintNumber;
}
