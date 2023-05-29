package utry.data.modular.ccBoard.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description:
 * @author: WangXinhao
 * @create: 2022-11-01 13:13
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPointVo {

    /**
     * 字段（图例）名称
     */
    private String label;

    /**
     * 数值
     */
    private String number;
}
