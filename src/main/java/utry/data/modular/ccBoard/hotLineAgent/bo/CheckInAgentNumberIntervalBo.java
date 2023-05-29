package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 各时间间隔签入坐席数量业务类
 * @author: WangXinhao
 * @create: 2022-11-03 11:28
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInAgentNumberIntervalBo {

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 签入坐席数量
     */
    private Integer checkInAgentNumber;
}
