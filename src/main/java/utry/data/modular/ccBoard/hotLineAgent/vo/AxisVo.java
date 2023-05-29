package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 轴视图
 * @author: WangXinhao
 * @create: 2022-11-07 16:06
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AxisVo {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点值
     */
    private String label;

    /**
     * 横线值
     */
    private String stateTime;

    /**
     * 节点状态 1:蓝色；0灰色（cc看板业务中不需要区别节点状态）
     */
    private String isComplete;
}
