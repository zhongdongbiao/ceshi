package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 开始结束时间持续时间业务类
 * @author: WangXinhao
 * @create: 2022-11-04 15:02
 **/

@Data
public class StartEndDateTimeDurationBo {

    /**
     * 开始时间
     */
    private LocalDateTime startDateTime;

    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;

    /**
     * 持续时长
     */
    private Integer duration;
}
