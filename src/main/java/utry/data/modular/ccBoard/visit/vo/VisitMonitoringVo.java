package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *回访项目监控
 *
 * @author zhongdongbiao
 * @date 2022/10/24 9:23
 */
@Data
public class VisitMonitoringVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当日呼出量
     */
    private Integer breatheNumber;

    /**
     * 当日呼通率
     */
    private Double breatheRate;

    /**
     * 当日单位小时完成量
     */
    private Double completeNumber;

    /**
     * 当日回访数据量
     */
    private Integer visitNumber;

    /**
     * 当月有效回访率
     */
    private Double visitRate;

}
