package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 有效回访率下钻页面Vo
 * @author zhongdongbiao
 * @date 2022/10/28 13:27
 */
@Data
public class EffectiveRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 时间
     */
    private String date;

    /**
     * 队列回访率
     */
    private List<TwoLegend> queueEffectives;
}
