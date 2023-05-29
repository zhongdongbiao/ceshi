package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 人力数据表格业务类
 * @author: WangXinhao
 * @create: 2023-03-03 14:58
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanDataTableBo {

    /**
     * 时段
     */
    private LocalDateTime time;

    /**
     * 热线量
     */
    private String accessNumber;

    /**
     * 接起量
     */
    private String connectionNumber;

    /**
     * 接通率
     */
    private String connectionRate;

    /**
     * 10s接通率
     */
    private String tenSecondRate;
}
