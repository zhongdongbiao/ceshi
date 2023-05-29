package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线呼叫业务监控视图
 * @author: WangXinhao
 * @create: 2022-11-11 09:20
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallServiceMonitorAxisVo {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 超时数量
     */
    private Integer timeOutNumber;

    /**
     * 节点数量
     */
    private Integer nodeNumber;

    /**
     * 横线值
     */
    private Double stateTime;

    /**
     * 等待数量
     */
    private Integer watingNumber;
}
