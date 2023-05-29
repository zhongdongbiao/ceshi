package utry.data.modular.ccBoard.common.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.ccBoard.common.bo.AgentMonitorBo;
import utry.data.modular.ccBoard.hotLineAgent.dto.StateDateDurationQueueIdPageDto;

import java.util.List;

/**
 * @program: data
 * @description: 坐席监控信息表持久层
 * @author: WangXinhao
 * @create: 2022-11-10 13:43
 **/

@Mapper
public interface DosCallCenterAgentMonitorDao {
    /**
     * 根据队列id和状态查询坐席当前状态
     *
     * @param dto 查询条件
     * @return AgentMonitorBo
     */
    @DS("shuce_db")
    List<AgentMonitorBo> selectAgentCurrentStateByQueueIdAndState(StateDateDurationQueueIdPageDto dto);
}
