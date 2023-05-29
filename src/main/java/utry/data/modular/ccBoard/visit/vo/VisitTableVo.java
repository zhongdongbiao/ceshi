package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访坐席数据
 *
 * @author zhongdongbiao
 * @date 2022/10/24 9:37
 */
@Data
public class VisitTableVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 在线坐席数量
     */
    private String onlineSeats;

    /**
     * 空闲坐席数量
     */
    private String freeSeats;
    /**
     * 空闲超时坐席
     */
    private String freeTimeoutSeats;

    /**
     * 通话中坐席数量
     */
    private String callSeats;

    /**
     * 通话中超时坐席
     */
    private String callTimeoutSeats;

    /**
     * 挂起坐席数量
     */
    private String hangSeats;

    /**
     * 挂起超时坐席
     */
    private String hangTimeoutSeats;

}
