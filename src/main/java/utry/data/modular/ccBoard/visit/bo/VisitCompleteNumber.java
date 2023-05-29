package utry.data.modular.ccBoard.visit.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访坐席利用率
 * @author zhongdongbiao
 * @date 2022/11/3 15:53
 */
@Data
public class VisitCompleteNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 完成量
     */
    private String complete;

}
