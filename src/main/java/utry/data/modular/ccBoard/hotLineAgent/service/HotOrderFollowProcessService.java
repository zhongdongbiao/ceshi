package utry.data.modular.ccBoard.hotLineAgent.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.*;
import utry.data.modular.ccBoard.hotLineAgent.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @program: data
 * @description: 热线服务单跟进流程信息业务接口
 * @author: WangXinhao
 * @create: 2022-10-24 15:00
 **/

public interface HotOrderFollowProcessService {

    /**
     * 插入热线服务单跟进流程
     *
     * @param map 推送的数据
     * @return 数量
     */
    int insertHotOrderFollowProcess(Map map);

    /**
     * 热线项目实时监控
     *
     * @param dto 查询条件
     * @return ProjectActualTimeMonitorVo
     */
    ProjectActualTimeMonitorVo projectActualTimeMonitor(DateDurationQueueIdDto dto);

    /**
     * 10s接通率表格
     *
     * @param dto 查询条件
     * @return PageInfo<TenSecondRateTableVo>
     */
    PageInfo<TenSecondRateTableVo> tenSecondRateTable(DateDurationQueueIdPageDto dto);

    /**
     * 满意度
     *
     * @param dto 查询条件
     * @param callType in呼入；out呼出
     * @return SatisfactionVo
     */
    SatisfactionVo satisfaction(DateQueueIdDto dto, String callType);

    /**
     * 人力数据-全天10s接通率
     *
     * @param dto 查询条件
     * @return 全天10s接通率
     */
    String allDayTenSecondRate(DateQueueIdDto dto);

    /**
     * 人力数据图表
     *
     * @param dto 查询条件
     * @return List<HumanDataChartVo>
     */
    List<HumanDataChartVo> humanDataChart(DateDurationQueueIdDto dto);

    /**
     * 时段在线及工时表格
     *
     * @param dto 查询条件
     * @return PageInfo<TimeSlotOnlineWorkingHourTableVo>
     */
    PageInfo<TimeSlotOnlineWorkingHourTableVo> timeSlotOnlineWorkingHourTable(DateDurationQueueIdPageDto dto);

    /**
     * 时段会话状态表格
     *
     * @param dto 查询条件
     * @return PageInfo<TimeDurationTableVo>
     */
    PageInfo<TimeDurationTableVo> timeDurationTable(AgentIdDateTimeDurationPageDto dto);

    /**
     * 进线统计图表
     *
     * @param dto 查询条件
     * @return List<IncomeStatisticChartVo>
     */
    List<IncomeStatisticChartVo> incomeStatisticChart(DateDurationQueueIdDto dto);

    /**
     * 量级数据图表
     *
     * @param dto 查询条件
     * @return List<MagnitudeDataChartVo>
     */
    List<MagnitudeDataChartVo> magnitudeDataChart(DateDurationQueueIdDto dto);

    /**
     * 坐席工时利用率图表
     *
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     * @return List<AgentManHourUtilizationRateChartVo>
     */
    List<AgentManHourUtilizationRateChartVo> agentManHourUtilizationRateChart(DateDurationQueueIdDto dto, String callType);

    /**
     * 坐席工时利用率图表-总体CPH/总体工时利用率
     *
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     * @return AgentManHourUtilizationRateTotalVo
     */
    AgentManHourUtilizationRateTotalVo agentManHourUtilizationRateTotal(DateDurationQueueIdDto dto, String callType);

    /**
     * 在线坐席-坐席状态表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentStateTableVo>
     */
    JSONObject agentStateTable(StateDateDurationQueueIdPageDto dto);

    /**
     * 队列历史表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentQueueHistoryTableVo>
     */
    PageInfo<AgentQueueHistoryTableVo> agentQueueHistoryTable(AgentIdDateDurationPageDto dto);

    /**
     * 热线服务单轴
     *
     * @param soundRecordFileName 录音文件名
     * @return List<AxisVo>
     */
    List<AxisVo> serviceOrderDetailAxis(String soundRecordFileName);

    /**
     * 热线坐席导出图表
     *
     * @param response response
     * @param dto      查询条件
     */
    void exportChart(HttpServletResponse response, HotLineExportConditionDto dto);

    /**
     * 坐席状态表格-状态历史表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentStateHistoryTableVo>
     */
    PageInfo<TimeDurationTableVo> agentStateHistoryTable(AgentIdDateDurationPageDto dto);

    /**
     * 在线坐席
     *
     * @param dto 查询条件
     * @return List<OnlineAgentVo>
     */
    OnlineAgentVo onlineAgent(QueueIdDto dto);

    /**
     * 实时排队图表
     *
     * @param dto 查询条件
     * @return List<RealTimeQueueChartVo>
     */
    List<RealTimeQueueChartVo> realTimeQueueChart(DateDurationQueueIdDto dto);

    /**
     * 热线呼叫业务监控
     *
     * @param dto 查询条件
     * @return List<AxisVo>
     */
    List<CallServiceMonitorAxisVo> callServiceMonitor(QueueIdDto dto);

    /**
     * 坐席工时利用率导出
     *
     * @param response response
     * @param dto      查询条件
     */
    void agentManHourUtilizationRateChartExport(HttpServletResponse response, HotLineExportConditionDto dto, String callType);

    /**
     * 热线坐席导出表格
     *
     * @param response response
     * @param dto      查询条件
     */
    void exportTable(HttpServletResponse response, HotLineExportConditionDto dto);

    /**
     * 接入量图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    List<DateValueChartVo> accessNumberChart(DateDurationQueueIdDto dto);

    /**
     * 接通率图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    List<DateValueChartVo> connectionRateChart(DateDurationQueueIdDto dto);

    /**
     * 10s率图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    List<DateValueChartVo> tenSecondRateChart(DateDurationQueueIdDto dto);

    /**
     * 平均排队时间图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    List<DateValueChartVo> avgQueueDurationChart(DateDurationQueueIdDto dto);

    /**
     * 平均通话时长图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    List<DateValueChartVo> avgCallInDurationChart(DateDurationQueueIdDto dto);

    /**
     * 热线项目实时监控图表
     *
     * @param dto 查询条件
     * @return ProjectActualTimeMonitorChartVo
     */
    ProjectActualTimeMonitorChartVo projectActualTimeMonitorChart(DateDurationQueueIdDto dto);

    /**
     * 人力数据表格
     *
     * @param dto 查询条件
     * @return List<HumanDataTableVo>
     */
    List<HumanDataTableVo> humanDataTable(DateDurationQueueIdDto dto);
}
