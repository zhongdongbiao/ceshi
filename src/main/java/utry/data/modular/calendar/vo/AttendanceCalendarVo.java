package utry.data.modular.calendar.vo;

import utry.data.modular.calendar.model.CalendarDto ;
import lombok.Data;

/**
 * @ClassName: AttendanceCalendarVo
 * @Description: TODO
 * @author: yangkesheng
 * @date: 2022/2/17  16:14
 * @version: 1.0
 */
//@Data
public class AttendanceCalendarVo extends CalendarDto {
    /**
     * 缺勤时长
     */
    private String absenceTime;
    /**
     * 考勤状态
     * 正常1、异常2、
     */
    private String attendanceStatus;
    /**
     * 补签状态
     * 通过1、驳回2、审批中3、未知0
     */
    private String approvalStatus;
    /**
     * 备注内容
     */
    private String remarkContent;
    /**
     * 请假类别
     */
    private String leaveType;
    /**
     * 请假开始时间
     */
    private String startTime;
    /**
     * 请假结束时间
     */
    private String endTime;
    /**
     * 请假状态
     */
    private String approvalState;
}
