package utry.data.modular.ccBoard.common.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.constant.CcBoardConstant;
import utry.data.modular.baseConfig.dao.CcBaseConfigDao;
import utry.data.modular.baseConfig.model.CcQueueDept;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.modular.baseConfig.service.CcBaseDataService;
import utry.data.modular.ccBoard.common.dao.DosCallCenterRecordDao;
import utry.data.modular.ccBoard.common.service.CommonService;
import utry.data.modular.ccBoard.common.vo.AgentCallLogTableVo;
import utry.data.modular.ccBoard.hotLineAgent.dto.AgentIdDateDurationPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.HotLineExportConditionDto;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.util.DataUtil;
import utry.data.util.DatePatternUtil;
import utry.data.util.SmsUtils;
import utry.data.util.TimeUtil;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongdongbiao
 * @date 2022/10/27 11:31
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private SysConfServiceImpl sysConfService;

    @Autowired
    private DosCallCenterRecordDao dosCallCenterRecordDao;

    @Autowired
    private CcBaseConfigDao ccBaseConfigDao;

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private CcBaseDataService ccBaseDataService;

    /**
     * 获取工作时间
     *
     * @return String[0]=开始时间；String[1]=结束时间
     */
    @Override
    public String[] getStartWorkTime() {
        return sysConfService.getSystemConfig("CC_WORK_TIME", "100060").split("-");
    }

    /**
     * 获取一天中的工作时间间隔（下标0是上班时间）
     *
     * @param intervalUnit  单位
     * @param intervalValue 间隔
     * @return 时间间隔
     */
    @Override
    public List<LocalTime> getWorkTimeInterval(TemporalUnit intervalUnit, long intervalValue) {
        String[] workTime = getStartWorkTime();
        String startWorkTime = workTime[0];
        String endWorkTime = workTime[1];
        LocalTime startTime = LocalTime.parse(startWorkTime, DatePatternUtil.NORM_MINUTE_FORMATTER);
        LocalTime endTime = LocalTime.parse(endWorkTime, DatePatternUtil.NORM_MINUTE_FORMATTER);

        LocalDate now = LocalDate.now();
        LocalDateTime startDateTime = now.atTime(startTime);
        LocalDateTime endDateTime = now.atTime(endTime);
        List<LocalDateTime> localDateTimes = TimeUtil.groupByInterval(startDateTime, endDateTime, intervalUnit, intervalValue);
        List<LocalTime> result = new ArrayList<>(localDateTimes.size());
        localDateTimes.forEach(localDateTime -> result.add(localDateTime.toLocalTime()));
        return result;
    }

    /**
     * 获取两个日期区间的工作时间间隔（下标0是上班时间）（排除未来时间、排除非工作时间）
     * 时间节点作为时段的开始，所以结束时间前推一个时段
     *
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @param intervalUnit  单位
     * @param intervalValue 间隔
     * @return 时间间隔
     */
    @Override
    public List<LocalDateTime> getWorkTimeIntervalByDateDuration(LocalDate startDate, LocalDate endDate, TemporalUnit intervalUnit, long intervalValue) {
        String[] workTime = getStartWorkTime();
        String startWorkTime = workTime[0];
        String endWorkTime = workTime[1];
        LocalTime startTime = LocalTime.parse(startWorkTime, DatePatternUtil.NORM_MINUTE_FORMATTER);
        LocalTime endTime = LocalTime.parse(endWorkTime, DatePatternUtil.NORM_MINUTE_FORMATTER).minusMinutes(30);

        LocalDateTime startDateTime = startDate.atTime(startTime);
        LocalDateTime endDateTime = endDate.atTime(endTime);
        List<LocalDateTime> localDateTimes = TimeUtil.groupByInterval(startDateTime, endDateTime, intervalUnit, intervalValue);
        LocalDateTime now = LocalDateTime.now();
        // 排除未来时间、排除每天非上班时间的点
        return localDateTimes.stream().filter(localDateTime -> {
            LocalTime localTime = localDateTime.toLocalTime();
            return localDateTime.isBefore(now.minusMinutes(intervalValue)) && (localTime.equals(startTime) || localTime.equals(endTime) || (localTime.isAfter(startTime) && localTime.isBefore(endTime)));
        }).collect(Collectors.toList());
    }

    /**
     * 获取一线人力队列id（写死）
     *
     * @return 一线人力队列id
     */
    @Override
    public List<String> getFrontLineManpowerQueueId() {
        // 一线队列写死
        List<String> frontLineManpowerQueueId = new ArrayList<>(6);
        frontLineManpowerQueueId.add("4000");
        frontLineManpowerQueueId.add("4100");
        frontLineManpowerQueueId.add("4200");
        frontLineManpowerQueueId.add("4500");
        frontLineManpowerQueueId.add("4600");
        frontLineManpowerQueueId.add("4700");
        return frontLineManpowerQueueId;
    }

    /**
     * 通话记录表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentCallLogTableVo>
     */
    @Override
    public PageInfo<AgentCallLogTableVo> agentCallLogTable(AgentIdDateDurationPageDto dto, String callType) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(AgentIdDateDurationPageDto.class, dto);
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(null);
        Map<String, String> relationMap = new HashMap<>(queueDeptList.size());
        for (CcQueueDept ccQueueDept : queueDeptList) {
            relationMap.put(ccQueueDept.getQueueId(), ccQueueDept.getQueueName());
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<AgentCallLogTableVo> agentCallLogTableVoList = null;
        if (callType.equals(CcBoardConstant.IN)) {
            agentCallLogTableVoList = dosCallCenterRecordDao.selectCallInAgentCallLogTable(dto);
        }
        if (callType.equals(CcBoardConstant.OUT)) {
            agentCallLogTableVoList = dosCallCenterRecordDao.selectCallOutAgentCallLogTable(dto);
        }
        Map<String, String> satisfaction = smsUtils.getDictList("SATISFACTION");
        if (agentCallLogTableVoList != null) {
            agentCallLogTableVoList.forEach(vo -> {
                vo.setQueueName(relationMap.get(vo.getQueueId()));
                vo.setRingDuration(TimeUtil.secondTransformMinSecond(Integer.parseInt(vo.getRingDuration())));
                vo.setCallDuration(TimeUtil.secondTransformMinSecond(Integer.parseInt(vo.getCallDuration())));
                vo.setSatisfaction(satisfaction.get(vo.getSatisfaction() != null ? vo.getSatisfaction() : "NULL"));
            });
        }
        return new PageInfo<>(agentCallLogTableVoList);
    }

    /**
     * 通话记录表格导出
     *
     * @param response response
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     */
    @Override
    public void agentCallLogTableExport(HttpServletResponse response, HotLineExportConditionDto dto, String callType) {
        List<AgentCallLogTableVo> exportData = null;
        AgentIdDateDurationPageDto buildDto = AgentIdDateDurationPageDto.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .agentId(dto.getAgentId())
                .queueId(dto.getQueueId())
                .build();
        if (callType.equals(CcBoardConstant.IN)) {
            exportData = dosCallCenterRecordDao.selectCallInAgentCallLogTable(buildDto);
        }
        if (callType.equals(CcBoardConstant.OUT)) {
            exportData = dosCallCenterRecordDao.selectCallOutAgentCallLogTable(buildDto);
        }

        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(null);
        Map<String, String> relationMap = new HashMap<>(queueDeptList.size());
        for (CcQueueDept ccQueueDept : queueDeptList) {
            relationMap.put(ccQueueDept.getQueueId(), ccQueueDept.getQueueName());
        }
        Map<String, String> satisfaction = smsUtils.getDictList("SATISFACTION");
        if (exportData != null) {
            exportData.forEach(vo -> {
                vo.setQueueName(relationMap.get(vo.getQueueId()));
                vo.setRingDuration(TimeUtil.secondTransformMinSecond(Integer.parseInt(vo.getRingDuration())));
                vo.setCallDuration(TimeUtil.secondTransformMinSecond(Integer.parseInt(vo.getCallDuration())));
                vo.setSatisfaction(satisfaction.get(vo.getSatisfaction()));
                vo.setTenSecondRateFlag("1".equals(vo.getTenSecondRateFlag()) ? "是" : "否");
                vo.setCallType(CcBoardConstant.IN.equals(vo.getCallType()) ? "呼入" : "呼出");
            });
        }
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("id", "id");
        headerMap.put("tenSecondRateFlag", "10s率达标");
        headerMap.put("queueName", "坐席所属队列");
        headerMap.put("agentName", "坐席姓名");
        headerMap.put("agentId", "工号");
        headerMap.put("source", "主叫号码");
        headerMap.put("callType", "呼叫类型");
        headerMap.put("callState", "呼叫状态");
        headerMap.put("ringDuration", "振铃时长");
        headerMap.put("callDuration", "通话时长");
        headerMap.put("hangUpParty", "挂断方");
        headerMap.put("callDateTime", "呼叫时间");
        headerMap.put("satisfaction", "满意度");

        String sheetName = "通话记录表格";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<AgentCallLogTableVo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取坐席各状态的超时时间map
     *
     * @return map ：key=状态名称；value=超时时长（秒）
     */
    @Override
    public Map<String, String> getAgentStatusNameReminderMap() {
        List<SeatStatusReminder> seatStatusReminders = ccBaseDataService.getSeatStatusReminder();
        Map<String, String> seatStatusTimeOutMap = new HashMap<>(8);
        seatStatusReminders.forEach(status -> seatStatusTimeOutMap.put(status.getStatusName(), status.getTimeout()));
        return seatStatusTimeOutMap;
    }

    /**
     * 获取坐席各状态的超时时间map
     *
     * @return map ：key=状态id；value=超时时长（秒）
     */
    @Override
    public Map<String, String> getAgentStatusIdReminderMap() {
        List<SeatStatusReminder> seatStatusReminders = ccBaseDataService.getSeatStatusReminder();
        Map<String, String> seatStatusTimeOutMap = new HashMap<>(8);
        seatStatusReminders.forEach(status -> seatStatusTimeOutMap.put(status.getSeatStatusId(), status.getTimeout()));
        return seatStatusTimeOutMap;
    }
}
