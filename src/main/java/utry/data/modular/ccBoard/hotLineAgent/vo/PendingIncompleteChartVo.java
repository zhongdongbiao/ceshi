package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 未完成待处理单
 * @author: WangXinhao
 * @create: 2022-10-24 14:10
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingIncompleteChartVo {

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private Integer count;
}
