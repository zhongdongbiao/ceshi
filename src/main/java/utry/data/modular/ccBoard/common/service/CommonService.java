package utry.data.modular.ccBoard.common.service;

import com.github.pagehelper.PageInfo;
import utry.data.modular.ccBoard.common.vo.AgentCallLogTableVo;
import utry.data.modular.ccBoard.hotLineAgent.dto.AgentIdDateDurationPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.HotLineExportConditionDto;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;

/**
 * @author zhongdongbiao
 * @date 2022/10/27 11:31
 */
public interface CommonService {

    /**
     * 获取工作时间
     *
     * @return String[0]=开始时间；String[1]=结束时间
     */
    String[] getStartWorkTime();

    /**
     * 获取一天中的工作时间间隔
     *
     * @param intervalUnit  单位
     * @param intervalValue 间隔
     * @return 时间间隔
     */
    List<LocalTime> getWorkTimeInterval(TemporalUnit intervalUnit, long intervalValue);

    /**
     * 获取两个日期区间的工作时间间隔（排除非工作时间）
     *
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param intervalUnit  单位
     * @param intervalValue 间隔
     * @return 时间间隔
     */
    List<LocalDateTime> getWorkTimeIntervalByDateDuration(LocalDate startDate, LocalDate endDate, TemporalUnit intervalUnit, long intervalValue);

    /**
     * 获取一线人力队列id（写死）
     *
     * @return 一线人力队列id
     */
    List<String> getFrontLineManpowerQueueId();

    /**
     * 通话记录表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentCallLogTableVo>
     */
    PageInfo<AgentCallLogTableVo> agentCallLogTable(AgentIdDateDurationPageDto dto, String callType);

    /**
     * 通话记录表格导出
     *
     * @param response response
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     */
    void agentCallLogTableExport(HttpServletResponse response, HotLineExportConditionDto dto, String callType);

    /**
     * 获取坐席各状态的超时时间map
     *
     * @return map ：key=状态名称；value=超时时长（秒）
     */
    Map<String, String> getAgentStatusNameReminderMap();

    /**
     * 获取坐席各状态的超时时间map
     *
     * @return map ：key=状态id；value=超时时长（秒）
     */
    Map<String, String> getAgentStatusIdReminderMap();
}
