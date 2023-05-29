package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 坐席id、时间区间、分页查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-25 10:29
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentIdDateTimeDurationPageDto {

    /**
     * 工号
     */
    private String agentId;

    /**
     * 开始日期时间 yyyy-MM-dd HH:mm:ss
     */
    private String startDateTime;

    /**
     * 结束日期时间 yyyy-MM-dd HH:mm:ss
     */
    private String endDateTime;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
