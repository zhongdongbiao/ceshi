package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 坐席状态视图
 * @author: WangXinhao
 * @create: 2022-11-10 15:42
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStateNumberVo {

    /**
     * 状态
     */
    private String state;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 超时数量
     */
    private Integer overtimeNumber;
}
