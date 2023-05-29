package utry.data.modular.ccBoard.visit.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.core.common.BusinessException;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.data.constant.CcBoardConstant;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.common.service.CommonService;
import utry.data.modular.ccBoard.common.vo.AgentCallLogTableVo;
import utry.data.modular.ccBoard.hotLineAgent.dto.AgentIdDateDurationPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.DateQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.HotLineExportConditionDto;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.ccBoard.hotLineAgent.vo.AgentManHourUtilizationRateChartVo;
import utry.data.modular.ccBoard.hotLineAgent.vo.AgentManHourUtilizationRateTotalVo;
import utry.data.modular.ccBoard.hotLineAgent.vo.SatisfactionVo;
import utry.data.modular.ccBoard.visit.dao.VisitAuditDao;
import utry.data.modular.ccBoard.visit.dto.*;
import utry.data.modular.ccBoard.visit.service.VisitService;
import utry.data.modular.ccBoard.visit.vo.*;
import utry.data.modular.technicalQuality.controller.TechnicalQualityController;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.DataUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongdongbiao
 * @date 2022/10/24 15:43
 */
@RestController
@RequestMapping("/visit")
@Api(tags = "回访坐席Controller")
public class VisitController extends CommonController {

    @Resource
    private VisitService visitService;

    @Autowired
    private CommonService commonService;

    @Resource
    private VisitAuditDao visitAuditDao;

    @Autowired
    private HotOrderFollowProcessService hotOrderFollowProcessService;


    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TechnicalQualityController.class);


    @ApiOperation(value = "获取全部队列", notes = "获取全部队列")
    @PostMapping("/getQueueList")
    public RetResult getQueueList() {
        // 获取回访队列
       List<QueueVo> visitQueue =  visitService.getQueueList("1");
        // 获取热线队列
        List<QueueVo> hotLineQueue =  visitService.getQueueList("2");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("visitQueue",visitQueue);
        jsonObject.put("hotLineQueue",hotLineQueue);
       return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取回访项目监控数据", notes = "获取回访项目监控数据")
    @PostMapping("/getVisitMonitoring")
    public RetResult getVisitMonitoring(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        VisitMonitoringVo visitMonitoring = visitService.getVisitMonitoring(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(visitMonitoring);
    }

    @ApiOperation(value = "有效回访率", notes = "有效回访率")
    @PostMapping("/getEffectiveRate")
    public RetResult getEffectiveRate(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        List<EffectiveRateVo> effectiveRateVos = visitService.getVisitReclined(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(effectiveRateVos);
    }

    @ApiOperation(value = "话务明细", notes = "话务明细")
    @PostMapping("/getCallDetail")
    public RetResult getCallDetail(@RequestBody DateDurationQueueIdPageDto dateDurationQueueIdPageDto) {
        PageBean pageBean = getPageBean(dateDurationQueueIdPageDto.getPageNum()+"",dateDurationQueueIdPageDto.getPageSize()+"");
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<CallRecordDetail> callDetailVos = visitService.getCallDetail(dateDurationQueueIdPageDto);
        PageInfo<CallRecordDetail> pageInfo = new PageInfo<>(callDetailVos);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取回访记录", notes = "获取回访记录")
    @PostMapping("/getVisitRecord")
    public RetResult getVisitRecord(@RequestBody JSONObject jsonObject) {
        String serviceNumber = jsonObject.getString("serviceNumber");
        VisitRecordVo visitRecordVo = visitService.getVisitRecord(serviceNumber);
        return RetResponse.makeOKRsp(visitRecordVo);
    }

    @ApiOperation(value = "呼叫中心-回访坐席-坐席状态表格")
    @RequestMapping(value = "/visitAgentStateTable", method = RequestMethod.POST)
    public RetResult visitAgentStateTable(@RequestBody VisitTableDto dto) {
        DataUtil.limitCrossDayQuery(VisitTableDto.class,dto);
        List<VisitAgentStateTableVo> visitAgentStateTableVos = visitService.visitAgentStateTable(dto, "0");
        // 分页
        long pageNum = dto.getPageNum();
        long pageSize = dto.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = visitAgentStateTableVos.size();
        visitAgentStateTableVos = visitAgentStateTableVos.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", visitAgentStateTableVos);
        jsonObject.put("count", size);
        return RetResponse.makeOKRsp(jsonObject);
    }


    @ApiOperation(value = "呼叫中心-回访坐席-坐席状态表格-导出")
    @RequestMapping(value = "/exportVisitAgentStateTable", method = RequestMethod.POST)
    public void exportVisitAgentStateTable(HttpServletResponse response,@RequestBody VisitTableDto dto) {

        List<VisitAgentStateTableVo> visitAgentStateTableVos = visitService.visitAgentStateTable(dto,"1");
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("agentName", "姓名");
        headerMap.put("agentId", "工号");
        headerMap.put("breathOut", "呼出量");
        headerMap.put("breathRate", "呼通率");
        headerMap.put("breathNumber", "呼通量");
        headerMap.put("workTime", "工作时长");
        headerMap.put("manHourUtilizationRate", "工时利用率");
        headerMap.put("callTimeUtilization", "通话工时利用率");
        headerMap.put("completeNumber", "单位小时完成量");
        headerMap.put("currentState", "当前状态");
        headerMap.put("currentStateStay", "当前状态持续");
        headerMap.put("leisureCumulativeDuration", "累积示闲时长");
        headerMap.put("restCumulativeDuration", "累积小休时长");
        headerMap.put("afterCallCumulativeDuration", "累积话后时长");
        headerMap.put("busyCumulativeDuration", "累积示忙时长");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("回访坐席状态"+operationTime+".xlsx",20,20, null, "回访坐席状态列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, visitAgentStateTableVos,true);
        } catch (Exception e) {
            LOGGER.error("回访坐席状态导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    @ApiOperation(value = "未完成回访项目", notes = "未完成回访项目")
    @PostMapping("/getNoVisitProject")
    public RetResult getNoVisitProject(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        List<NotCompleteVo> notCompleteVos = visitService.getNoVisitProject(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(notCompleteVos);
    }

    @ApiOperation(value = "回访业务监控", notes = "回访业务监控")
    @PostMapping("/getVisitBusinessMonitoring")
    public RetResult getVisitBusinessMonitoring(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        VisitBusinessMonitoringVo visitBusinessMonitoringVo = visitService.getVisitBusinessMonitoring(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(visitBusinessMonitoringVo);
    }

    @ApiOperation(value = "已完成回访项目", notes = "已完成回访项目")
    @PostMapping("/getCompleteProject")
    public RetResult getCompleteProject(@RequestBody CompleteProjectDto completeProjectDto) {
        List<CompleteProjectVo> completeProjects = visitService.getCompleteProject(completeProjectDto);
        return RetResponse.makeOKRsp(completeProjects);
    }

    @ApiOperation(value = "已完成回访项目-灰色", notes = "已完成回访项目-灰色")
    @PostMapping("/getCompleteProjectByGray")
    public RetResult getCompleteProjectByGray(@RequestBody CompleteProjectDto completeProjectDto) {
        List<GrayProjectVo> completeProjects = visitService.getCompleteProjectByGray(completeProjectDto);
        return RetResponse.makeOKRsp(completeProjects);
    }


    @ApiOperation(value = "回访结果", notes = "回访结果")
    @PostMapping("/getVisitResult")
    public RetResult getVisitResult(@RequestBody VisitResultDto visitResultDto) {
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(visitResultDto.getQueueId());
        PageBean pageBean = getPageBean(visitResultDto.getPageNum(),visitResultDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<VisitResultVo> completeProjects = visitService.getVisitResult(visitResultDto,accountingCenter);
        PageInfo<VisitResultVo> pageInfo = new PageInfo<>(completeProjects);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "回访结果导出", notes = "回访结果导出")
    @PostMapping("/exportVisitResult")
    public void exportVisitResult(HttpServletResponse response, @RequestBody VisitResultDto visitResultDto) {
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(visitResultDto.getQueueId());
        List<VisitResultVo> completeProjects = visitService.getVisitResult(visitResultDto,accountingCenter);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("systemState", "状态");
        headerMap.put("visitTable", "回访坐席");
        headerMap.put("agentNumber", "坐席编号");
        headerMap.put("accountingRegional", "核算大区");
        headerMap.put("documentNo", "单据号");
        headerMap.put("productCategory", "产品品类");
        headerMap.put("date", "单据日期");
        headerMap.put("serviceType", "服务类别");
        headerMap.put("serviceNumber", "服务单号");
        headerMap.put("visitTme", "回访时间");
        headerMap.put("completeNote", "回访结果描述");
        headerMap.put("resultType", "违约结果分类");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("回访结果"+operationTime+".xlsx",20,20, null, "回访结果列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, completeProjects,true);
        } catch (Exception e) {
            LOGGER.error("回访结果导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    @ApiOperation(value = "申诉统计", notes = "申诉统计")
    @PostMapping("/getComplaint")
    public RetResult getComplaint(@RequestBody CompleteProjectDto dateDurationQueueIdDto) {
        List<ComplaintVo> completeProjects = visitService.getComplaint(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(completeProjects);
    }

    @ApiOperation(value = "回访违约单", notes = "回访违约单")
    @PostMapping("/getVisitDefault")
    public RetResult getVisitDefault(@RequestBody VisitDefaultDto visitDefaultDto) {
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(visitDefaultDto.getQueueId());
        PageBean pageBean = getPageBean(visitDefaultDto.getPageNum(),visitDefaultDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<VisitDefaultVo> visitDefaultVos = visitService.getVisitDefault(visitDefaultDto,accountingCenter);
        PageInfo<VisitDefaultVo> pageInfo = new PageInfo<>(visitDefaultVos);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "回访违约单导出", notes = "回访违约单导出")
    @PostMapping("/exportVisitDefault")
    public void exportVisitDefault(HttpServletResponse response, @RequestBody VisitDefaultDto visitDefaultDto) {
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(visitDefaultDto.getQueueId());
        List<VisitDefaultVo> visitDefaultVos = visitService.getVisitDefault(visitDefaultDto, accountingCenter);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("systemState", "状态");
        headerMap.put("documentNo", "单据号");
        headerMap.put("date", "单据日期");
        headerMap.put("serviceType", "服务类别");
        headerMap.put("serviceNumber", "服务单号");
        headerMap.put("defaultDescription", "违约描述");
        headerMap.put("serviceDate", "作业日期");
        headerMap.put("money", "赔偿金额");
        headerMap.put("result", "审核结果");
        List<TitleEntity> titleList = new ArrayList<>();
        String pid = "-1";
        int i = 0;
        for(Map.Entry<String, String> entry:headerMap.entrySet()){
            TitleEntity entity = new TitleEntity(String.valueOf(i), pid, entry.getValue(), entry.getKey());
            titleList.add(entity);
            i++;
        }
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取操作时间
        String operationTime = dateFormat.format(new Date());
        // 添加行内数据
        ExcelTool excelTool = new ExcelTool("申诉统计"+operationTime+".xlsx",20,20, null, "申诉统计列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, visitDefaultVos,true);
        } catch (Exception e) {
            LOGGER.error("申诉统计导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    @ApiOperation(value = "回访坐席利用率", notes = "回访坐席利用率")
    @PostMapping("/getVisitRate")
    public RetResult getVisitRate(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class,dateDurationQueueIdDto);
        List<VisitRateVo> visitRateVos = visitService.getVisitRate(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(visitRateVos);
    }

    @ApiOperation(value = "违约率", notes = "违约率")
    @PostMapping("/getDefaultRate")
    public RetResult getDefaultRate(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        List<DefaultRateVo> defaultRateVos = visitService.getDefaultRate(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(defaultRateVos);
    }

    @ApiOperation(value = "回访呼出量/呼通量", notes = "回访呼出量/呼通量")
    @PostMapping("/getBreatheRate")
    public RetResult getBreatheRate(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class,dateDurationQueueIdDto);
        List<BreatheRateVo> defaultRateVos = visitService.getBreatheRate(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(defaultRateVos);
    }

    @ApiOperation(value = "回访坐席满意度", notes = "回访坐席满意度")
    @PostMapping("/getSatisfactionRate")
    public RetResult getSatisfactionRate(@RequestBody DateDurationQueueIdDto dateDurationQueueIdDto) {
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class,dateDurationQueueIdDto);
        List<SatisfactionRateVo> defaultRateVos = visitService.getSatisfactionRate(dateDurationQueueIdDto);
        return RetResponse.makeOKRsp(defaultRateVos);
    }

    @ApiOperation(value = "导出图表", notes = "导出图表")
        @PostMapping("/exportChart")
    public void exportChart(HttpServletResponse response, @RequestBody ExportConditionDto exportConditionDto) {
        visitService.exportChart(response,exportConditionDto);
    }

    @ApiOperation(value = "坐席工时利用率图表")
    @RequestMapping(value = "/agentManHourUtilizationRateChart", method = RequestMethod.POST)
    public RetResult<List<AgentManHourUtilizationRateChartVo>> agentManHourUtilizationRateChart(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentManHourUtilizationRateChart(dto, CcBoardConstant.OUT));
    }

    @ApiOperation(value = "坐席工时利用率图表-总体CPH/总体工时利用率", notes = "仅今日")
    @RequestMapping(value = "/agentManHourUtilizationRateTotal", method = RequestMethod.POST)
    public RetResult<AgentManHourUtilizationRateTotalVo> agentManHourUtilizationRateTotal(@RequestBody DateDurationQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.agentManHourUtilizationRateTotal(dto,CcBoardConstant.OUT));
    }

    @ApiOperation(value = "热线项目数据概览-满意度")
    @RequestMapping(value = "/satisfaction", method = RequestMethod.POST)
    public RetResult<SatisfactionVo> satisfaction(@RequestBody DateQueueIdDto dto) {
        return RetResponse.makeOKRsp(hotOrderFollowProcessService.satisfaction(dto, CcBoardConstant.OUT));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格-通话记录表格")
    @RequestMapping(value = "/agentCallLogTable", method = RequestMethod.POST)
    public RetResult<PageInfo<AgentCallLogTableVo>> agentCallLogTable(@RequestBody AgentIdDateDurationPageDto dto) {
        return RetResponse.makeOKRsp(commonService.agentCallLogTable(dto, CcBoardConstant.OUT));
    }

    @ApiOperation(value = "热线项目数据概览-在线坐席-坐席状态表格-通话记录表格")
    @RequestMapping(value = "/exportAgentCallLogTable", method = RequestMethod.POST)
    public void exportAgentCallLogTable(HttpServletResponse response,@RequestBody HotLineExportConditionDto dto) {
        commonService.agentCallLogTableExport(response,dto, CcBoardConstant.OUT);
    }
}
