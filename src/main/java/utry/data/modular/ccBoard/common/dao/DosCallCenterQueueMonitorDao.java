package utry.data.modular.ccBoard.common.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.bo.QueueMonitorBo;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.bo.CallServiceMonitorBo;
import utry.data.modular.ccBoard.hotLineAgent.bo.OnlineAgentBo;
import utry.data.modular.ccBoard.hotLineAgent.dto.QueueIdDto;

import java.util.List;

/**
 * @program: data
 * @description: 队列监控信息表持久层
 * @author: WangXinhao
 * @create: 2022-11-10 12:49
 **/

@Mapper
public interface DosCallCenterQueueMonitorDao {

    /**
     * 根据队列id批量查询队列当前排队人数
     *
     * @param queueId 队列id
     * @return QueueMonitorBo
     */
    @DS("shuce_db")
    List<QueueMonitorBo> selectBatchQueueNumberByQueueId(@Param("queueId") List<String> queueId);

    /**
     * 根据队列id查询在线坐席
     *
     * @param dto 查询条件
     * @return OnlineAgentBo
     */
    @DS("shuce_db")
    OnlineAgentBo selectOnlineAgentByQueueId(QueueIdDto dto);

    /**
     * 查询实时排队数据
     *
     * @param dto 查询条件
     * @return QueueMonitorBo
     */
    @DS("shuce_db")
    List<QueueMonitorBo> selectRealTimeQueueChart(DateDurationQueueIdDto dto);

    /**
     * 查询父级实时排队图表
     *
     * @param dto    查询条件
     * @param deptId 部门id
     * @return QueueMonitorBo
     */
    @DS("shuce_db")
    List<QueueMonitorBo> selectParentRealTimeQueueChart(@Param("dto") DateDurationQueueIdDto dto,
                                                        @Param("deptId") String deptId);

    /**
     * 查询热线呼叫业务监控
     *
     * @param dto 查询条件
     * @return CallServiceMonitorBo
     */
    @DS("shuce_db")
    CallServiceMonitorBo selectCallServiceMonitor(DateDurationQueueIdDto dto);

    /**
     * 查询历史实时排队数据
     *
     * @param buildDto 查询条件
     * @return QueueMonitorBo
     */
    @DS("shuce_db")
    List<QueueMonitorBo> selectHistoryRealTimeQueueChart(DateDurationQueueIdDto buildDto);

    /**
     * 查询历史父级实时排队图表
     *
     * @param dto    查询条件
     * @param deptId 部门id
     * @return QueueMonitorBo
     */
    @DS("shuce_db")
    List<QueueMonitorBo> selectHistoryParentRealTimeQueueChart(@Param("dto") DateDurationQueueIdDto dto,
                                                               @Param("deptId") String deptId);
}
