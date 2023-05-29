package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.String;

/**
 * @program: data
 * @description: 人力数据表格视图
 * @author: WangXinhao
 * @create: 2023-03-03 11:19
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanDataTableVo {

    /**
     * 时段
     */
    private String time;

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

    /**
     * 总人力
     */
    private String totalManpower;
}
