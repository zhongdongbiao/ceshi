package utry.data.modular.ccBoard.visit.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 未完成回访项目
 *
 * @author zhongdongbiao
 * @date 2022/10/24 14:16
 */
@Data
public class NotComplete implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String type;

    /**
     * 待完成
     */
    private String pending;

    /**
     * 超三天未完成
     */
    private String threeNotComplete;

    /**
     * 未完成总量
     */
    private String notCompleteTotal;

    /**
     * 未完成率
     */
    private String rate;

}
