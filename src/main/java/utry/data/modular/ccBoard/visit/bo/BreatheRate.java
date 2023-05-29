package utry.data.modular.ccBoard.visit.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 回访呼出量
 * @author zhongdongbiao
 * @date 2022/11/3 15:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreatheRate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 呼出量
     */
    private String breatheOut;

    /**
     * 呼通量
     */
    private String callFlux;

    /**
     * 呼通率
     */
    private String breatheRate;
}
