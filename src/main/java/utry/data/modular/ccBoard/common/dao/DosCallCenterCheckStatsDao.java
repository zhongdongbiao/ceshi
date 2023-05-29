package utry.data.modular.ccBoard.common.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.bo.StartEndDateTimeDurationBo;
import utry.data.modular.ccBoard.hotLineAgent.dto.AgentIdDateDurationPageDto;
import utry.data.modular.ccBoard.hotLineAgent.vo.AgentQueueHistoryTableVo;
import utry.data.modular.ccBoard.visit.dto.VisitTableDto;
import utry.data.modular.ccBoard.visit.vo.VisitAgentStateTableVo;

import java.util.List;

/**
 * @program: data
 * @description: 签入签出统计持久层
 * @author: WangXinhao
 * @create: 2022-10-27 15:56
 **/

@Mapper
public interface DosCallCenterCheckStatsDao {

    /**
     * 总工作时长
     * @param dateDurationQueueIdDto
     * @return
     */
    @DS("shuce_db")
    int getWorkTime(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 登录开始结束时间、持续时长
     *
     * @param dto 查询条件
     * @return StartEndDateTimeDurationBo
     */
    @DS("shuce_db")
    List<StartEndDateTimeDurationBo> selectStartEndDateTimeDuration(DateDurationQueueIdDto dto);

    /**
     * 获取回访坐席坐席状态
     * @param dto
     * @return
     */
    @DS("shuce_db")
    List<VisitAgentStateTableVo> getVisitAgentStateTable(@Param("dto") VisitTableDto dto, @Param("historyFlag") String historyFlag);

    /**
     * 队列历史表格
     *
     * @param dto 查询条件
     * @return AgentQueueHistoryTableVo
     */
    @DS("shuce_db")
    List<AgentQueueHistoryTableVo> selectAgentQueueHistoryTable(AgentIdDateDurationPageDto dto);
}
