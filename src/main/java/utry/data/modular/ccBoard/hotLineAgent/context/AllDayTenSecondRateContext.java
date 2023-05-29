package utry.data.modular.ccBoard.hotLineAgent.context;

import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utry.data.modular.ccBoard.hotLineAgent.dto.DateQueueIdDto;

/**
 * @program: data
 * @description: 全天10s接通率上下文
 * @author: WangXinhao
 * @create: 2022-10-31 15:09
 **/

@Data
@Component
@Lazy
public class AllDayTenSecondRateContext {

    /**
     * 入参
     */
    private DateQueueIdDto request;

    /**
     * 中间变量
     */

    /**
     * 10s内坐席接起量
     */
    private Integer tenSecondConnectionNumber;

    /**
     * 接入量
     */
    private Integer accessNumber;

    /**
     * 结果
     */
    private String result;

    public static AllDayTenSecondRateContext init(DateQueueIdDto dto) {
        AllDayTenSecondRateContext context = new AllDayTenSecondRateContext();
        context.setRequest(dto);
        return context;
    }
}
