package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 全天工时标志视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:29
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeDurationVo {

    /**
     * 历时
     */
    private String duration;

    /**
     * 标志：1true，0false
     */
    private Integer flag;
}
