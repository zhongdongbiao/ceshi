package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 通话记录
 *
 * @author zhongdongbiao
 * @date 2022/10/24 11:20
 */
@Data
public class CallRecordVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 10s率达标
     */
    private String tenRate;

    /**
     * 坐席所属队列
     */
    private String queue;

    /**
     * 坐席姓名
     */
    private String name;

    /**
     * 工号
     */
    private String number;

    /**
     * 主叫号码
     */
    private String phone;

    /**
     * 呼叫类型
     */
    private String callType;

    /**
     * 呼叫状态
     */
    private String callState;

    /**
     * 振铃时长
     */
    private String ringTime;

    /**
     * 通话时长
     */
    private String duration;

    /**
     * 挂断方
     */
    private String hang;

    /**
     * 呼叫时间
     */
    private String callTime;

    /**
     * 满意度
     */
    private String satisfaction;

}
