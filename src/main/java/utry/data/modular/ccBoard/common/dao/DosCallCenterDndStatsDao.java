package utry.data.modular.ccBoard.common.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.bo.DndStatsBo;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.bo.StartEndDateTimeDurationBo;
import utry.data.modular.ccBoard.hotLineAgent.vo.TimeDurationTableVo;

import java.util.List;

/**
 * @program: data
 * @description: 示忙示闲统计持久层
 * @author: WangXinhao
 * @create: 2022-10-27 15:57
 **/
@Mapper
public interface DosCallCenterDndStatsDao {

    /**
     * 时段会话状态表格
     *
     * @param startDateTime 开始日期
     * @param endDateTime   结束日期
     * @param agentId       坐席id
     * @param queueId       队列id
     * @return TimeDurationTableVo
     */
    @DS("shuce_db")
    List<TimeDurationTableVo> selectTimeDurationTable(@Param("startDateTime") String startDateTime,
                                                      @Param("endDateTime") String endDateTime,
                                                      @Param("agentId") String agentId,
                                                      @Param("queueId") List<String> queueId);

    /**
     * 查询除就餐和培训外的的坐席（包括振铃时长、通话时长）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DndStatsBo
     */
    @DS("shuce_db")
    List<DndStatsBo> selectCheckInSeat(@Param("startDate") String startDate,
                                       @Param("endDate") String endDate,
                                       @Param("queueId") List<String> queueId);

    /**
     * 通话时长、振铃时长、空闲、话后、培训、值日、忙碌开始结束时间、持续时长
     *
     * @param dto        查询条件
     * @param callType   in呼入；out呼出
     * @param statusList 工时利用率状态
     * @return StartEndDateTimeDurationBo
     */
    @DS("shuce_db")
    List<StartEndDateTimeDurationBo> selectStartEndDateTimeDuration(@Param("dto") DateDurationQueueIdDto dto,
                                                                    @Param("callType") String callType,
                                                                    @Param("statusList") List<String> statusList);
}
