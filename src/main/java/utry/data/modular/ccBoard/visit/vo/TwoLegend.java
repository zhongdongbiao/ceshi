package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 有效回访率
 * @author zhongdongbiao
 * @date 2022/11/1 16:45
 */
@Data
public class TwoLegend implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 图例
     */
    private String label;

    /**
     * 值
     */
    private Double number;
}
