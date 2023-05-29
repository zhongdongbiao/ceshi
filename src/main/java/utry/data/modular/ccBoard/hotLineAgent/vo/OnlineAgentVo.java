package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 热线项目数据概览-在线坐席（仅今天）视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:31
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineAgentVo {

    /**
     * 签入坐席数量
     */
    private Integer checkInAgents;

    /**
     * 坐席状态数量
     */
    List<AgentStateNumberVo> agentState;
}
