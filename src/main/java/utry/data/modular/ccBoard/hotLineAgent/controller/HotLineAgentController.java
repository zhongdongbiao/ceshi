package utry.data.modular.ccBoard.hotLineAgent.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.constant.CcBoardConstant;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.common.service.CommonService;
import utry.data.modular.ccBoard.common.vo.AgentCallLogTableVo;
import utry.data.modular.ccBoard.hotLineAgent.dto.*;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.ccBoard.hotLineAgent.vo.*;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @program: data
 * @description: 热线坐席版面控制类
 * @author: WangXinhao
 * @create: 2022-10-24 16:33
 **/

@RestController
@RequestMapping("/hotLineSeat")
@Api(tags = "热线坐席版面控制类")
public class HotLineAgentController {

    @Autowired
    private HotOrderFollowProcessService hotOrderFollowProcessService;

    @Autowired
    private CommonService commonService;

    @Resource
    private SysConfServiceImpl sysConfService;

    @ApiOperation(value = "热线项目实时监控")
    @RequestMapping(value = "/projectActualTimeMonitor", method = RequestMethod.POST)
    public RetResult<ProjectActualTimeMonitorVo> projectActualTimeMonitor(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.projectActualTimeMonitor(dto));
    }

    @ApiOperation(value = "10s接通率表格")
    @RequestMapping(value = "/tenSecondRateTable", method = RequestMethod.POST)
    public RetResult<PageInfo<TenSecondRateTableVo>> tenSecondRateTable(@RequestBody DateDurationQueueIdPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.tenSecondRateTable(dto));
    }

    @ApiOperation(value = "热线呼叫业务监控")
    @RequestMapping(value = "/callServiceMonitor", method = RequestMethod.POST)
    public RetResult<List<CallServiceMonitorAxisVo>> callServiceMonitor(@RequestBody QueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.callServiceMonitor(dto));
    }

    @ApiOperation(value = "热线项目数据概览-满意度")
    @RequestMapping(value = "/satisfaction", method = RequestMethod.POST)
    public RetResult<SatisfactionVo> satisfaction(@RequestBody DateQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.satisfaction(dto, CcBoardConstant.IN));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席")
    @RequestMapping(value = "/onlineAgent", method = RequestMethod.POST)
    public RetResult<OnlineAgentVo> onlineAgent(@RequestBody QueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.onlineAgent(dto));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格")
    @RequestMapping(value = "/agentStateTable", method = RequestMethod.POST)
    public RetResult<JSONObject> agentStateTable(@RequestBody StateDateDurationQueueIdPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentStateTable(dto));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格-状态历史表格")
    @RequestMapping(value = "/agentStateHistoryTable", method = RequestMethod.POST)
    public RetResult<PageInfo<TimeDurationTableVo>> agentStateHistoryTable(@RequestBody AgentIdDateDurationPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentStateHistoryTable(dto));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格-队列历史表格")
    @RequestMapping(value = "/agentQueueHistoryTable", method = RequestMethod.POST)
    public RetResult<PageInfo<AgentQueueHistoryTableVo>> agentQueueHistoryTable(@RequestBody AgentIdDateDurationPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentQueueHistoryTable(dto));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格-通话记录表格")
    @RequestMapping(value = "/agentCallLogTable", method = RequestMethod.POST)
    public RetResult<PageInfo<AgentCallLogTableVo>> agentCallLogTable(@RequestBody AgentIdDateDurationPageDto dto) {
        return RetResponse.makeOKRsp(commonService.agentCallLogTable(dto, CcBoardConstant.IN));
    }

    @ApiOperation(value = "热线项目数据概览-人力数据-全天10s接通率")
    @RequestMapping(value = "/allDayTenSecondRate", method = RequestMethod.POST)
    public RetResult<String> allDayTenSecondRate(@RequestBody DateQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.allDayTenSecondRate(dto));
    }

    @ApiOperation(value = "热线项目数据概览-人力数据图表")
    @RequestMapping(value = "/humanDataChart", method = RequestMethod.POST)
    public RetResult<List<HumanDataChartVo>> humanDataChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.humanDataChart(dto));
    }

    @ApiOperation(value = "热线项目数据概览-时段在线及工时表格")
    @RequestMapping(value = "/timeSlotOnlineWorkingHourTable", method = RequestMethod.POST)
    public RetResult<PageInfo<TimeSlotOnlineWorkingHourTableVo>> timeSlotOnlineWorkingHourTable(@RequestBody DateDurationQueueIdPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.timeSlotOnlineWorkingHourTable(dto));
    }

    @ApiOperation(value = "热线项目数据概览-时段在线工时表格-时段会话状态表格", notes = "点击时间单元格浮现表格")
    @RequestMapping(value = "/timeDurationTable", method = RequestMethod.POST)
    public RetResult<PageInfo<TimeDurationTableVo>> timeDurationTable(@RequestBody AgentIdDateTimeDurationPageDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.timeDurationTable(dto));
    }

    @ApiOperation(value = "热线项目数据概览-时段在线工时表格-热线服务单明细表格-热线服务单轴")
    @RequestMapping(value = "/serviceOrderDetailAxis", method = RequestMethod.GET)
    public RetResult<List<AxisVo>> serviceOrderDetailAxis(@RequestParam("soundRecordFileName") String soundRecordFileName) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.serviceOrderDetailAxis(soundRecordFileName));
    }

    @ApiOperation(value = "实时排队图表")
    @RequestMapping(value = "/realTimeQueueChart", method = RequestMethod.POST)
    public RetResult<List<RealTimeQueueChartVo>> realTimeQueueChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.realTimeQueueChart(dto));
    }

    @ApiOperation(value = "进线统计图表")
    @RequestMapping(value = "/incomeStatisticChart", method = RequestMethod.POST)
    public RetResult<List<IncomeStatisticChartVo>> incomeStatisticChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.incomeStatisticChart(dto));
    }

    @ApiOperation(value = "量级数据图表")
    @RequestMapping(value = "/magnitudeDataChart", method = RequestMethod.POST)
    public RetResult<List<MagnitudeDataChartVo>> magnitudeDataChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.magnitudeDataChart(dto));
    }

    @ApiOperation(value = "坐席工时利用率图表-总体CPH/总体工时利用率", notes = "仅今日")
    @RequestMapping(value = "/agentManHourUtilizationRateTotal", method = RequestMethod.POST)
    public RetResult<AgentManHourUtilizationRateTotalVo> agentManHourUtilizationRateTotal(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentManHourUtilizationRateTotal(dto, CcBoardConstant.IN));
    }

    @ApiOperation(value = "坐席工时利用率图表")
    @RequestMapping(value = "/agentManHourUtilizationRateChart", method = RequestMethod.POST)
    public RetResult<List<AgentManHourUtilizationRateChartVo>> agentManHourUtilizationRateChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentManHourUtilizationRateChart(dto, CcBoardConstant.IN));
    }

    @ApiOperation(value = "热线坐席导出图表")
    @RequestMapping(value = "/exportChart", method = RequestMethod.POST)
    public void exportChart(HttpServletResponse response, @RequestBody HotLineExportConditionDto dto) {
        hotOrderFollowProcessService.exportChart(response, dto);
    }

    @ApiOperation(value = "热线坐席导出表格")
    @RequestMapping(value = "/exportTable", method = RequestMethod.POST)
    public void exportTable(HttpServletResponse response, @RequestBody HotLineExportConditionDto dto) {
        hotOrderFollowProcessService.exportTable(response, dto);
    }

    @ApiOperation(value = "接入量图表")
    @RequestMapping(value = "/accessNumberChart", method = RequestMethod.POST)
    public RetResult<List<DateValueChartVo>> accessNumberChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.accessNumberChart(dto));
    }

    @ApiOperation(value = "接通率图表")
    @RequestMapping(value = "/connectionRateChart", method = RequestMethod.POST)
    public RetResult<List<DateValueChartVo>> connectionRateChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.connectionRateChart(dto));
    }

    @ApiOperation(value = "10s率图表")
    @RequestMapping(value = "/tenSecondRateChart", method = RequestMethod.POST)
    public RetResult<List<DateValueChartVo>> tenSecondRateChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.tenSecondRateChart(dto));
    }

    @ApiOperation(value = "平均排队时间图表")
    @RequestMapping(value = "/avgQueueDurationChart", method = RequestMethod.POST)
    public RetResult<List<DateValueChartVo>> avgQueueDurationChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.avgQueueDurationChart(dto));
    }

    @ApiOperation(value = "平均通话时长图表")
    @RequestMapping(value = "/avgCallInDurationChart", method = RequestMethod.POST)
    public RetResult<List<DateValueChartVo>> avgCallInDurationChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.avgCallInDurationChart(dto));
    }

    @ApiOperation(value = "热线项目实时监控图表")
    @RequestMapping(value = "/projectActualTimeMonitorChart", method = RequestMethod.POST)
    public RetResult<ProjectActualTimeMonitorChartVo> projectActualTimeMonitorChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.projectActualTimeMonitorChart(dto));
    }

    @ApiOperation(value = "热线项目实时监控（坐席维度）")
    @RequestMapping(value = "/projectActualTimeMonitorAgent", method = RequestMethod.POST)
    public JSONObject projectActualTimeMonitorAgent(@RequestBody JSONObject dto) {
        String IP = sysConfService.getSystemConfig("SHUCE_URL", "100060");
        String url = IP + "searchCallInTarget";
        String postResult;
        JSONObject result = null;
        try {
            postResult = HttpClientUtil.postJSONObject(url, dto.toJSONString());
            result = JSONObject.parseObject(postResult);
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }
}
