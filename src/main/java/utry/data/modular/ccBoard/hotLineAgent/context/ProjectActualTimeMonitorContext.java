package utry.data.modular.ccBoard.hotLineAgent.context;

import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.vo.ProjectActualTimeMonitorVo;

/**
 * @program: data
 * @description: 热线项目实时监控上下文
 * @author: WangXinhao
 * @create: 2022-10-27 14:54
 **/

@Data
@Component
@Lazy
public class ProjectActualTimeMonitorContext {

    /**
     * 入参
     */
    private DateDurationQueueIdDto request;

    /**
     * 中间变量
     */
    /**
     * 接入量
     */
    private Integer accessNumber;

    /**
     * 坐席接起量
     */
    private Integer connectionNumber;

    /**
     * ACD排队时间
     */
    private Integer acdQueueTime;

    /**
     * 10s内坐席接起量
     */
    private Integer tenSecondConnectionNumber;

    /**
     * 通话时间
     */
    private Integer talkTime;

    /**
     * 接入客户数
     */
    private Integer callInCustomerNumber;

    /**
     * 接起客户数
     */
    private Integer connectCustomerNumber;
    /**
     * 组装结果
     */
    private ProjectActualTimeMonitorVo result;

    public static ProjectActualTimeMonitorContext init(DateDurationQueueIdDto dto) {
        ProjectActualTimeMonitorContext context = new ProjectActualTimeMonitorContext();
        context.setRequest(dto);
        context.setResult(new ProjectActualTimeMonitorVo());
        return context;
    }
}
