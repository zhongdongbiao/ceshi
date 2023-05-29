package utry.data.modular.ccBoard.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description:
 * @author: WangXinhao
 * @create: 2022-11-01 16:37
 **/


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPointTwoValueVo {

    /**
     * 字段（图例）名称
     */
    private String label;

    /**
     * 值1
     */
    private String number;

    /**
     * 值2
     */
    private String number2;
}
