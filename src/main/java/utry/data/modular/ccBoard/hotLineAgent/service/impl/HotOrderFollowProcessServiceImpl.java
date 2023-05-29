package utry.data.modular.ccBoard.hotLineAgent.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.data.constant.CcBoardConstant;
import utry.data.constant.CcChartConstant;
import utry.data.constant.RedisKeyConstant;
import utry.data.constant.RedisTimeOutConstant;
import utry.data.modular.baseConfig.dao.CcBaseConfigDao;
import utry.data.modular.baseConfig.dao.CcBaseDataDao;
import utry.data.modular.baseConfig.model.CcCoreTarget;
import utry.data.modular.baseConfig.model.CcQueueDept;
import utry.data.modular.baseConfig.model.HumanResCoef;
import utry.data.modular.ccBoard.common.bo.DndStatsBo;
import utry.data.modular.ccBoard.common.bo.QueueMonitorBo;
import utry.data.modular.ccBoard.common.dao.*;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.service.CommonService;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;
import utry.data.modular.ccBoard.hotLineAgent.bo.*;
import utry.data.modular.ccBoard.hotLineAgent.context.AllDayTenSecondRateContext;
import utry.data.modular.ccBoard.hotLineAgent.context.ProjectActualTimeMonitorContext;
import utry.data.modular.ccBoard.hotLineAgent.contextholder.AllDayTenSecondRateContextHolder;
import utry.data.modular.ccBoard.hotLineAgent.contextholder.ProjectActualTimeMonitorContextHolder;
import utry.data.modular.ccBoard.hotLineAgent.dao.HotOrderFollowProcessDao;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.*;
import utry.data.modular.ccBoard.hotLineAgent.model.HotOrderFollowProcess;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.ccBoard.hotLineAgent.vo.*;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.util.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: data
 * @description: 热线业务接口实现类
 * @author: WangXinhao
 * @create: 2022-10-24 15:00
 **/

@Slf4j
@Service
public class HotOrderFollowProcessServiceImpl implements HotOrderFollowProcessService {

    @Autowired
    private HotOrderFollowProcessDao hotOrderFollowProcessDao;

    @Autowired
    private ProjectActualTimeMonitorContextHolder projectActualTimeMonitorContextHolder;

    @Autowired
    private AllDayTenSecondRateContextHolder allDayTenSecondRateContextHolder;

    @Autowired
    private CcBaseConfigDao ccBaseConfigDao;

    @Autowired
    private DosCallCenterRecordDao dosCallCenterRecordDao;

    @Autowired
    private DosCallCenterDndStatsDao dosCallCenterDndStatsDao;

    @Autowired
    private DosCallCenterCheckStatsDao dosCallCenterCheckStatsDao;

    @Autowired
    private DosCallCenterQueueMonitorDao dosCallCenterQueueMonitorDao;

    @Autowired
    private DosCallCenterAgentMonitorDao dosCallCenterAgentMonitorDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private CcBaseDataDao ccBaseDataDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 插入热线服务单跟进流程
     *
     * @param map 推送的数据
     * @return 数量
     */
    @Override
    public int insertHotOrderFollowProcess(Map map) {
        String hotlineNumber = map.get("hotlineNumber") == null ? StringUtils.EMPTY : map.get("hotlineNumber").toString();
        String lastServiceAgentId = map.get("lastServiceAgentId") == null ? StringUtils.EMPTY : map.get("lastServiceAgentId").toString();
        String lastServiceAgentName = map.get("lastServiceAgentName") == null ? StringUtils.EMPTY : map.get("lastServiceAgentName").toString();
        String lastServiceTime = map.get("lastServiceTime") == null ? StringUtils.EMPTY : map.get("lastServiceTime").toString();

        LocalDateTime now = LocalDateTime.now();
        HotOrderFollowProcess hotOrderFollowProcess = HotOrderFollowProcess.builder()
                .hotOrderFollowProcessId(IdUtil.getSnowflakeNextId())
                .hotlineNumber(hotlineNumber)
                .lastServiceAgentId(lastServiceAgentId)
                .lastServiceAgentName(lastServiceAgentName)
                .lastServiceTime(LocalDateTime.parse(lastServiceTime, DatePattern.NORM_DATETIME_FORMATTER))
                .createTime(now)
                .updateTime(now)
                .build();
        return hotOrderFollowProcessDao.insert(hotOrderFollowProcess);

    }

    /**
     * 热线项目实时监控
     *
     * @param dto 查询条件
     * @return ProjectActualTimeMonitorVo
     */
    @Override
    public ProjectActualTimeMonitorVo projectActualTimeMonitor(DateDurationQueueIdDto dto) {
        ProjectActualTimeMonitorContext context = ProjectActualTimeMonitorContext.init(dto);
        projectActualTimeMonitorContextHolder.init(context);

        assembleAccessNumber(projectActualTimeMonitorContextHolder);
        assembleConnectionRate(projectActualTimeMonitorContextHolder);
        assembleTenSecondRate(projectActualTimeMonitorContextHolder);
        assembleTenSecondRateTarget(context);
        assembleAvgQueueDuration(projectActualTimeMonitorContextHolder);
        assembleAvgCallInDuration(projectActualTimeMonitorContextHolder);
        assembleCallInCustomerNumber(projectActualTimeMonitorContextHolder);
        assembleConnectCustomerNumber(projectActualTimeMonitorContextHolder);
        return context.getResult();
    }

    /**
     * 10s接通率表格
     *
     * @param dto 查询条件
     * @return PageInfo<TenSecondRateTableVo>
     */
    @Override
    public PageInfo<TenSecondRateTableVo> tenSecondRateTable(DateDurationQueueIdPageDto dto) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdPageDto.class, dto);
        List<String> queueIdList = dto.getQueueId();
        queueIdList = queueIdList.stream().sorted().collect(Collectors.toList());
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(queueIdList);
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<TenSecondRateTableVo> dbDataList = dosCallCenterRecordDao.selectTenSecondRateTable(dto, statusList);
        // queueId关联
        List<TenSecondRateTableVo> result = new ArrayList<>(queueDeptList.size());
        int i = 0, j = 0;
        while (i < queueDeptList.size()) {
            CcQueueDept ccQueueDept = queueDeptList.get(i);
            TenSecondRateTableVo dbData = null;
            if (j < dbDataList.size()) {
                dbData = dbDataList.get(j);
            }
            if (dbData == null || ccQueueDept.getQueueId().compareTo(dbData.getQueueId()) < 0) {
                i++;
                TenSecondRateTableVo buildVo = TenSecondRateTableVo.builder()
                        .queueId(ccQueueDept.getQueueId())
                        .queueName(ccQueueDept.getQueueName())
                        .build();
                result.add(buildVo);
                continue;
            }
            if (ccQueueDept.getQueueId().compareTo(dbData.getQueueId()) > 0) {
                j++;
                continue;
            }
            if (ccQueueDept.getQueueId().equals(dbData.getQueueId())) {
                String queueName = ccQueueDept.getQueueName();
                dbData.setQueueName(queueName);
                dbData.setAvgCallInDuration(TimeUtil.secondTransformMinSecond(Integer.parseInt(dbData.getAvgCallInDuration())));
                result.add(dbData);
                i++;
                j++;
            }
        }
        return new PageInfo<>(result);
    }

    /**
     * 满意度
     *
     * @param dto 查询条件
     * @return SatisfactionVo
     */
    @Override
    public SatisfactionVo satisfaction(DateQueueIdDto dto, String callType) {
        dto.setDate(LocalDate.now().toString());
        SatisfactionVo satisfactionVo = null;
        if (CcBoardConstant.IN.equals(callType)) {
            satisfactionVo = dosCallCenterRecordDao.selectCallInDailyWeeklyMonthlySatisfaction(dto);
        } else if (CcBoardConstant.OUT.equals(callType)) {
            satisfactionVo = dosCallCenterRecordDao.selectCallOutDailyWeeklyMonthlySatisfaction(dto);
        }
        return satisfactionVo;
    }

    /**
     * 人力数据-全天10s接通率
     *
     * @param dto 查询条件
     * @return 全天10s接通率
     */
    @Override
    public String allDayTenSecondRate(DateQueueIdDto dto) {
        AllDayTenSecondRateContext context = AllDayTenSecondRateContext.init(dto);
        allDayTenSecondRateContextHolder.init(context);

        assembleTenSecondRate(allDayTenSecondRateContextHolder);
        return context.getResult();
    }

    /**
     * 人力数据图表
     *
     * @param dto 查询条件
     * @return List<HumanDataChartVo>
     */
    @Override
    public List<HumanDataChartVo> humanDataChart(DateDurationQueueIdDto dto) {
        String key = RedisKeyConstant.HUMAN_DATA_CHART + dto.getQueueId();
        // 查redis
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (List<HumanDataChartVo>) redisTemplate.opsForValue().get(key);
        }
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);

        List<HumanDataChartBo> humanDataChartBoList = getHumanDataChartBo(dto);
        List<CheckInAgentNumberIntervalBo> checkInAgentNumberIntervalBoList = getCheckInAgentNumberInterval(dto, localDateTimes);
        String yearMonth = LocalDate.parse(dto.getEndDate()).format(DatePattern.NORM_MONTH_FORMATTER);
        Double tenSecondRateTargetDouble = ccBaseConfigDao.selectConnRateByTargetMonth(yearMonth);
        List<HumanDataChartVo> humanDataChartVoList = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            CheckInAgentNumberIntervalBo checkInAgentNumberIntervalBo = checkInAgentNumberIntervalBoList.get(i);
            HumanDataChartBo humanDataChartBo = null;
            LocalDateTime dbLocalDateTime = null;
            if (j < humanDataChartBoList.size()) {
                humanDataChartBo = humanDataChartBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(humanDataChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            if (humanDataChartBo == null || localDateTime.isBefore(dbLocalDateTime)) {
                i++;
                HumanDataChartVo build = HumanDataChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .checkInAgentNumber(ChartPointVo.builder().label(CcChartConstant.CHECK_IN_AGENT_NUMBER).number(String.valueOf(checkInAgentNumberIntervalBo.getCheckInAgentNumber())).build())
                        .tenSecondRate(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE).number(String.valueOf(0)).build())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(String.valueOf(0)).build())
                        .tenSecondRateTarget(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE_TARGET).number(String.valueOf(tenSecondRateTargetDouble)).build())
                        .build();
                humanDataChartVoList.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalDateTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalDateTime)) {
                HumanDataChartVo build = HumanDataChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .checkInAgentNumber(ChartPointVo.builder().label(CcChartConstant.CHECK_IN_AGENT_NUMBER).number(String.valueOf(checkInAgentNumberIntervalBo.getCheckInAgentNumber())).build())
                        .tenSecondRate(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE).number(Optional.ofNullable(humanDataChartBo.getTenSecondRate()).orElse(String.valueOf(0))).build())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(String.valueOf(Optional.ofNullable(humanDataChartBo.getAccessNumber()).orElse(0))).build())
                        .tenSecondRateTarget(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE_TARGET).number(String.valueOf(tenSecondRateTargetDouble)).build())
                        .build();
                humanDataChartVoList.add(build);
                i++;
                j++;
            }
        }
        long timeout = TimeUtil.getAfterHalfHourSecond() + RedisTimeOutConstant.HUMAN_DATA_CHART_TIME_OUT_OFFSET;
        synchronized (redisTemplate) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return humanDataChartVoList;
            }
            redisTemplate.opsForValue().set(key, humanDataChartVoList, timeout, TimeUnit.SECONDS);
        }
        return humanDataChartVoList;
    }

    /**
     * 获取各时间间隔签入坐席数量
     * 例：时间节点 08:30 计算 08:00-08:30 的数据
     *
     * @param dto            查询条件
     * @param localDateTimes 工作时间段
     * @return CheckInAgentNumberIntervalBo
     */
    private List<CheckInAgentNumberIntervalBo> getCheckInAgentNumberInterval(DateDurationQueueIdDto dto, List<LocalDateTime> localDateTimes) {
        List<CheckInAgentNumberIntervalBo> result = new ArrayList<>(localDateTimes.size());
        // 查库-坐席各状态持续时长
        List<DndStatsBo> dndStatsBoList = dosCallCenterDndStatsDao.selectCheckInSeat(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        LocalDateTime now = LocalDateTime.now();
        for (LocalDateTime intervalStart : localDateTimes) {
            int checkInAgentNumber = 0;
            // 未来时间不做计算
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            if (!intervalEnd.isAfter(now)) {
                // 计算各坐席在该时段内持续时间
                Map<String, Long> agentWorkDurationMap = getAgentIntervalDuration(intervalStart, intervalEnd, dndStatsBoList);
                // 计算除就餐和培训的其他时间超过15分钟的人数
                for (Map.Entry<String, Long> entry : agentWorkDurationMap.entrySet()) {
                    if (entry.getValue() > CcBoardConstant.CHECK_IN_PASS_LINE_SECOND) {
                        checkInAgentNumber++;
                    }
                }
            }
            result.add(CheckInAgentNumberIntervalBo.builder().time(intervalStart).checkInAgentNumber(checkInAgentNumber).build());
        }
        return result;
    }

    /**
     * 时段在线及工时表格
     *
     * @param dto 查询条件
     * @return PageInfo<TimeSlotOnlineWorkingHourTableVo>
     */
    @Override
    public PageInfo<TimeSlotOnlineWorkingHourTableVo> timeSlotOnlineWorkingHourTable(DateDurationQueueIdPageDto dto) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdPageDto.class, dto);
        // 查库
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<TimeSlotOnlineWorkingHourTableVo> timeSlotOnlineWorkingHourTableVoList = getTimeSlotOnlineWorkingHourTable(dto);
        // 全天工作时间工作标记
        List<String> agentIdList = new ArrayList<>(timeSlotOnlineWorkingHourTableVoList.size());
        timeSlotOnlineWorkingHourTableVoList.forEach(vo -> agentIdList.add(vo.getAgentId()));
        Map<String, List<TimeDurationVo>> agentTimeDurationFlagMap = getAgentTimeDurationFlag(dto, agentIdList, 1);

        // 根据agentId连接表格字段
        for (TimeSlotOnlineWorkingHourTableVo vo : timeSlotOnlineWorkingHourTableVoList) {
            String agentId = vo.getAgentId();
            List<TimeDurationVo> timeDurationVoList = agentTimeDurationFlagMap.get(agentId);
            vo.setTimeDuration(timeDurationVoList);
        }
        return new PageInfo<>(timeSlotOnlineWorkingHourTableVoList);
    }

    /**
     * 时段会话状态表格
     *
     * @param dto 查询条件
     * @return PageInfo<TimeDurationTableVo>
     */
    @Override
    public PageInfo<TimeDurationTableVo> timeDurationTable(AgentIdDateTimeDurationPageDto dto) {
        fixUndefined(dto);
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQueryLocalDateTime(AgentIdDateTimeDurationPageDto.class, dto);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<TimeDurationTableVo> timeDurationTableVoList = dosCallCenterDndStatsDao.selectTimeDurationTable(dto.getStartDateTime(), dto.getEndDateTime(), dto.getAgentId(), null);
        Map<String, String> inzCountryMap = smsUtils.getDictList("CC_STATUS");
        timeDurationTableVoList.forEach(vo -> vo.setState(inzCountryMap.get(vo.getState())));
        return new PageInfo<>(timeDurationTableVoList);
    }

    /**
     * 解决前端传值结束时间Undefined
     *
     * @param dto 入参
     */
    private void fixUndefined(AgentIdDateTimeDurationPageDto dto) {
        LocalTime endTime = LocalDateTime.parse(dto.getStartDateTime(), DatePattern.NORM_DATETIME_FORMATTER).plusMinutes(30).toLocalTime();
        LocalDateTime endLocalDateTime = LocalDate.parse(dto.getEndDateTime().substring(0, 10)).atTime(endTime);
        dto.setEndDateTime(endLocalDateTime.format(DatePattern.NORM_DATETIME_FORMATTER));
    }

    /**
     * 进线统计图表
     *
     * @param dto 查询条件
     * @return List<IncomeStatisticChartVo>
     */
    @Override
    public List<IncomeStatisticChartVo> incomeStatisticChart(DateDurationQueueIdDto dto) {
        String key = RedisKeyConstant.INCOME_STATISTIC_CHART + dto.getQueueId();
        // 查redis
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (List<IncomeStatisticChartVo>) redisTemplate.opsForValue().get(key);
        }
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        List<IncomeStatisticChartBo> incomeStatisticChartBoList = dosCallCenterRecordDao.selectIncomeStatisticChart(dto);
        List<IncomeStatisticChartVo> incomeStatisticChartVoList = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            IncomeStatisticChartBo incomeStatisticChartBo = null;
            LocalDateTime dbLocalTime = null;
            if (j < incomeStatisticChartBoList.size()) {
                incomeStatisticChartBo = incomeStatisticChartBoList.get(j);
                dbLocalTime = LocalDateTime.parse(incomeStatisticChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            if (incomeStatisticChartBo == null || localDateTime.isBefore(dbLocalTime)) {
                i++;
                IncomeStatisticChartVo build = IncomeStatisticChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(String.valueOf(0)).build())
                        .connectNumber(ChartPointVo.builder().label(CcChartConstant.CONNECT_NUMBER).number(String.valueOf(0)).build())
                        .connectionRate(ChartPointVo.builder().label(CcChartConstant.CONNECTION_RATE).number(String.valueOf(0.00)).build())
                        .build();
                incomeStatisticChartVoList.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalTime)) {
                IncomeStatisticChartVo build = IncomeStatisticChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(incomeStatisticChartBo.getAccessNumber().toString()).build())
                        .connectNumber(ChartPointVo.builder().label(CcChartConstant.CONNECT_NUMBER).number(incomeStatisticChartBo.getConnectNumber().toString()).build())
                        .connectionRate(ChartPointVo.builder().label(CcChartConstant.CONNECTION_RATE).number(incomeStatisticChartBo.getConnectionRate()).build())
                        .build();
                incomeStatisticChartVoList.add(build);
                i++;
                j++;
            }
        }
        long timeout = TimeUtil.getAfterHalfHourSecond() + RedisTimeOutConstant.INCOME_STATISTIC_CHART_TIME_OUT_OFFSET;
        synchronized (redisTemplate) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return incomeStatisticChartVoList;
            }
            redisTemplate.opsForValue().set(key, incomeStatisticChartVoList, timeout, TimeUnit.SECONDS);
        }
        return incomeStatisticChartVoList;
    }

    /**
     * 量级数据图表
     *
     * @param dto 查询条件
     * @return List<MagnitudeDataChartVo>
     */
    @Override
    public List<MagnitudeDataChartVo> magnitudeDataChart(DateDurationQueueIdDto dto) {
        String key = RedisKeyConstant.MAGNITUDE_DATA_CHART + dto.getQueueId();
        // 查redis
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (List<MagnitudeDataChartVo>) redisTemplate.opsForValue().get(key);
        }
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        List<MagnitudeDataChartBo> magnitudeDataChartBoList = dosCallCenterRecordDao.selectMagnitudeDataChart(dto);
        // 获取一线人力队列id（写死）
        List<String> frontLineManpowerQueueId = commonService.getFrontLineManpowerQueueId();
        // 入参队列与一线队列求交集
        dto.setQueueId(DataUtil.getIntersection(dto.getQueueId(), frontLineManpowerQueueId));

        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        // 计算各时段一线人力 = 员工 * 人力系数
        Map<LocalDateTime, Double> frontLineManpowerMap = getFrontLineManpowerMap(dto, localDateTimes);
        // null为true
        boolean frontLineManpowerFlag = frontLineManpowerMap == null;
        // 根据时间拼接数据
        List<MagnitudeDataChartVo> magnitudeDataChartVoList = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            MagnitudeDataChartBo magnitudeDataChartBo = null;
            double frontLineManpower = frontLineManpowerFlag ? 0d : frontLineManpowerMap.get(localDateTime);
            LocalDateTime dbLocalDateTime = null;
            if (j < magnitudeDataChartBoList.size()) {
                magnitudeDataChartBo = magnitudeDataChartBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(magnitudeDataChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            String theoryConnectNumber = BigDecimal.valueOf(frontLineManpower).multiply(BigDecimal.valueOf(CcBoardConstant.FRONT_LINE_MANPOWER_MULTI_VALUE)).setScale(2, RoundingMode.HALF_UP).toString();
            String frontLineManpowerString = BigDecimal.valueOf(frontLineManpower).setScale(2, RoundingMode.HALF_UP).toString();
            if (magnitudeDataChartBo == null || localDateTime.isBefore(dbLocalDateTime)) {
                i++;
                MagnitudeDataChartVo build = MagnitudeDataChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .theoryConnectNumber(ChartPointVo.builder().label(CcChartConstant.THEORY_CONNECT_NUMBER).number(theoryConnectNumber).build())
                        .connectNumber(ChartPointVo.builder().label(CcChartConstant.CONNECT_NUMBER).number(String.valueOf(0)).build())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(String.valueOf(0)).build())
                        .frontLineManpower(ChartPointVo.builder().label(CcChartConstant.FRONT_LINE_MANPOWER).number(frontLineManpowerString).build())
                        .tenSecondRate(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE).number(String.valueOf(0)).build())
                        .build();
                magnitudeDataChartVoList.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalDateTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalDateTime)) {
                MagnitudeDataChartVo build = MagnitudeDataChartVo.builder()
                        .time(localDateTime.toLocalTime().toString())
                        .theoryConnectNumber(ChartPointVo.builder().label(CcChartConstant.THEORY_CONNECT_NUMBER).number(theoryConnectNumber).build())
                        .connectNumber(ChartPointVo.builder().label(CcChartConstant.CONNECT_NUMBER).number(String.valueOf(Optional.ofNullable(magnitudeDataChartBo.getConnectNumber()).orElse(0))).build())
                        .accessNumber(ChartPointVo.builder().label(CcChartConstant.ACCESS_NUMBER).number(String.valueOf(Optional.ofNullable(magnitudeDataChartBo.getAccessNumber()).orElse(0))).build())
                        .frontLineManpower(ChartPointVo.builder().label(CcChartConstant.FRONT_LINE_MANPOWER).number(frontLineManpowerString).build())
                        .tenSecondRate(ChartPointVo.builder().label(CcChartConstant.TEN_SECOND_RATE).number(Optional.ofNullable(magnitudeDataChartBo.getTenSecondRate()).orElse(String.valueOf(0))).build())
                        .build();
                magnitudeDataChartVoList.add(build);
                i++;
                j++;
            }
        }
        long timeout = TimeUtil.getAfterHalfHourSecond() + RedisTimeOutConstant.MAGNITUDE_DATA_CHART_TIME_OUT_OFFSET;
        synchronized (redisTemplate) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return magnitudeDataChartVoList;
            }
            redisTemplate.opsForValue().set(key, magnitudeDataChartVoList, timeout, TimeUnit.SECONDS);
        }
        return magnitudeDataChartVoList;
    }

    /**
     * 坐席工时利用率图表
     *
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     * @return List<AgentManHourUtilizationRateChartVo>
     */
    @Override
    public List<AgentManHourUtilizationRateChartVo> agentManHourUtilizationRateChart(DateDurationQueueIdDto dto, String callType) {
        String key = RedisKeyConstant.AGENT_MAN_HOUR_UTILIZATION_RATE_CHART + dto.getQueueId();
        // 查redis
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (List<AgentManHourUtilizationRateChartVo>) redisTemplate.opsForValue().get(key);
        }
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        List<AgentManHourUtilizationRateChartVo> result = new ArrayList<>(localDateTimes.size());
        // 单位小时接入量/呼出量
        List<IntervalCallDurationRingTimeHourlyCallNumberBo> intervalCallDurationRingTimeHourlyCallNumberBoList = dosCallCenterRecordDao.selectIntervalCallDurationRingTimeBo(dto, callType);
        // 获取工作的状态
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        // 分子：通话时长、振铃时长、空闲时长、话后时长、忙碌时长、培训时长、小休-值日
        List<StartEndDateTimeDurationBo> otherStateBoList = dosCallCenterDndStatsDao.selectStartEndDateTimeDuration(dto, callType, statusList);
        // 分母：登录时长
        List<StartEndDateTimeDurationBo> loginBoList = dosCallCenterCheckStatsDao.selectStartEndDateTimeDuration(dto);
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime intervalStart = localDateTimes.get(i);
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            IntervalCallDurationRingTimeHourlyCallNumberBo intervalCallDurationRingTimeBo = null;
            LocalDateTime dbLocalDateTime = null;
            int molecule = 0, denominator = 0;
            int hourlyCallNumber = 0;
            String manHourUtilizationRate;
            String hourlyCallNumberLabel = callType.equals(CcBoardConstant.IN) ? CcChartConstant.HOURLY_ACCESS_NUMBER : CcChartConstant.HOURLY_CALL_OUT_NUMBER;
            if (j < intervalCallDurationRingTimeHourlyCallNumberBoList.size()) {
                intervalCallDurationRingTimeBo = intervalCallDurationRingTimeHourlyCallNumberBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(intervalCallDurationRingTimeBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }

            // 获取单位小时接入量/呼出量
            if (dbLocalDateTime != null && intervalStart.isEqual(dbLocalDateTime)) {
                hourlyCallNumber = intervalCallDurationRingTimeBo.getHourlyCallNumber();
                j++;
            }
            // 分子累加
            molecule += getIntervalStateDuration(intervalStart, intervalEnd, otherStateBoList);
            // 分母累加登录时长
            denominator += getIntervalStateDuration(intervalStart, intervalEnd, loginBoList);
            // 工时利用率
            manHourUtilizationRate = denominator == 0 || (molecule > denominator) ? String.valueOf(100) : BigDecimal.valueOf(molecule).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP).toString();
            AgentManHourUtilizationRateChartVo build = AgentManHourUtilizationRateChartVo.builder()
                    .time(intervalStart.toLocalTime().toString())
                    .hourlyCallNumber(ChartPointVo.builder().label(hourlyCallNumberLabel).number(String.valueOf(hourlyCallNumber)).build())
                    .manHourUtilizationRate(ChartPointVo.builder().label(CcChartConstant.MAN_HOUR_UTILIZATION_RATE).number(manHourUtilizationRate).build())
                    .build();
            result.add(build);
            i++;
        }
        long timeout = TimeUtil.getAfterHalfHourSecond() + RedisTimeOutConstant.AGENT_MAN_HOUR_UTILIZATION_RATE_CHART_TIME_OUT_OFFSET;
        synchronized (redisTemplate) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return result;
            }
            redisTemplate.opsForValue().set(key, result, timeout, TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 获取工作状态
     *
     * @return 工时利用率的工作状态
     */
    private List<String> getCcWorkStatus(String code) {
        Map<String, String> workStatusMap = smsUtils.getDictList(code);
        List<String> statusList = new ArrayList<>(workStatusMap.size());
        workStatusMap.forEach((k, v) -> statusList.add(k));
        return statusList;
    }

    /**
     * 坐席工时利用率图表-总体CPH/总体工时利用率
     *
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     * @return AgentManHourUtilizationRateTotalVo
     */
    @Override
    public AgentManHourUtilizationRateTotalVo agentManHourUtilizationRateTotal(DateDurationQueueIdDto dto, String callType) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<String> inCphStatus = getCcWorkStatus(CcBoardConstant.CC_IN_CPH_STATUS);
        List<String> outCphStatus = getCcWorkStatus(CcBoardConstant.CC_OUT_CPH_STATUS);
        return dosCallCenterRecordDao.selectAgentManHourUtilizationRateTotal(dto, callType, statusList, inCphStatus, outCphStatus);
    }

    /**
     * 在线坐席-坐席状态表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentStateTableVo>
     */
    @Override
    public JSONObject agentStateTable(StateDateDurationQueueIdPageDto dto) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(StateDateDurationQueueIdPageDto.class, dto);
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<String> serviceStatusList = getCcWorkStatus(CcBoardConstant.CC_SERVICE_STATUS);
        List<AgentStateTableVo> agentStateTableVoList = dosCallCenterRecordDao.selectAgentStateTable(dto, statusList, serviceStatusList, 0);
        Map<String, String> seatStatusTimeOutMap = commonService.getAgentStatusNameReminderMap();
        for (AgentStateTableVo vo : agentStateTableVoList) {
            // 判断当前状态是否超时
            String timeOut = seatStatusTimeOutMap.get(vo.getCurrentState());
            if (timeOut != null) {
                int stayDuration = TimeUtil.hourMinSecTransformSecond(vo.getCurrentStateStay());
                if (stayDuration > Integer.parseInt(timeOut)) {
                    vo.setCurrentStateTimeOutFlag(1);
                } else {
                    vo.setCurrentStateTimeOutFlag(0);
                }
            } else {
                vo.setCurrentStateTimeOutFlag(0);
            }
            vo.setBusyCumulativeDuration(TimeUtil.secondTransform(vo.getBusyCumulativeDuration()));
            vo.setRestCumulativeDuration(TimeUtil.secondTransform(vo.getRestCumulativeDuration()));
            vo.setLeisureCumulativeDuration(TimeUtil.secondTransform(vo.getLeisureCumulativeDuration()));
            vo.setAfterCallCumulativeDuration(TimeUtil.secondTransform(vo.getAfterCallCumulativeDuration()));
        }

        // 超时置顶
        int j = 0;
        for (int i = 0; i < agentStateTableVoList.size(); i++) {
            if (agentStateTableVoList.get(i).getCurrentStateTimeOutFlag() == 1) {
                AgentStateTableVo temp = agentStateTableVoList.get(i);
                agentStateTableVoList.set(i, agentStateTableVoList.get(j));
                agentStateTableVoList.set(j, temp);
                j++;
            }
        }

        // 分页
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        int startRow = pageNum * pageSize - pageSize;
        int size = agentStateTableVoList.size();
        agentStateTableVoList = agentStateTableVoList.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", agentStateTableVoList);
        jsonObject.put("total", size);
        return jsonObject;
    }

    /**
     * 队列历史表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentQueueHistoryTableVo>
     */
    @Override
    public PageInfo<AgentQueueHistoryTableVo> agentQueueHistoryTable(AgentIdDateDurationPageDto dto) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(AgentIdDateDurationPageDto.class, dto);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<AgentQueueHistoryTableVo> agentQueueHistoryTableVoList = dosCallCenterCheckStatsDao.selectAgentQueueHistoryTable(dto);
        return new PageInfo<>(agentQueueHistoryTableVoList);
    }

    /**
     * 热线服务单轴
     *
     * @param soundRecordFileName 录音文件名
     * @return List<AxisVo>
     */
    @Override
    public List<AxisVo> serviceOrderDetailAxis(String soundRecordFileName) {
        ServiceOrderDetailAxisBo serviceOrderDetailAxisBo = dosCallCenterRecordDao.selectServiceOrderDetailAxis(soundRecordFileName);
        if (serviceOrderDetailAxisBo == null) {
            return Collections.EMPTY_LIST;
        }
        List<AxisVo> axisVoList = new ArrayList<>(4);
        axisVoList.add(AxisVo.builder().nodeName("来电").label(serviceOrderDetailAxisBo.getCallTime()).stateTime(null).isComplete("0").build());
        axisVoList.add(AxisVo.builder().nodeName("ACD排队").label(serviceOrderDetailAxisBo.getFirstQueueStartTime()).stateTime(null).isComplete("0").build());
        axisVoList.add(AxisVo.builder().nodeName("分配坐席&坐席接起").label(serviceOrderDetailAxisBo.getAnswerTime()).stateTime(serviceOrderDetailAxisBo.getRingTime()).isComplete("0").build());
        axisVoList.add(AxisVo.builder().nodeName("话后处理").label(serviceOrderDetailAxisBo.getAgentHangupTime()).stateTime(serviceOrderDetailAxisBo.getBillingSeconds()).isComplete("0").build());
        return axisVoList;
    }

    /**
     * 热线坐席导出图表
     *
     * @param response response
     * @param dto      查询条件
     */
    @Override
    public void exportChart(HttpServletResponse response, HotLineExportConditionDto dto) {
        Integer flag = dto.getFlag();
        if (flag == 1) {
            // 人力数据导出
            humanDataChartExport(response, dto);
        } else if (flag == 2) {
            // 实时排队导出
            realTimeQueueChartExport(response, dto);
        } else if (flag == 3) {
            // 进线统计导出
            incomeStatisticChartExport(response, dto);
        } else if (flag == 4) {
            // 量级数据导出
            magnitudeDataChartExport(response, dto);
        } else if (flag == 5) {
            // 坐席工时利用率导出
            agentManHourUtilizationRateChartExport(response, dto, CcBoardConstant.IN);
        }
    }

    /**
     * 热线坐席导出表格
     *
     * @param response response
     * @param dto      查询条件
     */
    @Override
    public void exportTable(HttpServletResponse response, HotLineExportConditionDto dto) {
        Integer flag = dto.getFlag();
        if (flag == 1) {
            // 10s接通率表格导出
            tenSecondRateTableExport(response, dto);
        } else if (flag == 2) {
            // 坐席状态表格导出
            agentStateTableExport(response, dto);
        } else if (flag == 3) {
            // 通话记录表格导出
            commonService.agentCallLogTableExport(response, dto, CcBoardConstant.IN);
        } else if (flag == 4) {
            // 时段在线及工时表格导出
            timeSlotOnlineWorkingHourTableExport(response, dto);
        } else if (flag == 5) {
            // 坐席各状态历史表格导出
            agentStateHistoryTableExport(response, dto);
        }
    }

    /**
     * 坐席各状态履历表格导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void agentStateHistoryTableExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        String startDateTime = LocalDate.parse(dto.getStartDate()).atTime(LocalTime.MIN).format(DatePattern.NORM_DATETIME_FORMATTER);
        String endDateTime = LocalDate.parse(dto.getEndDate()).atTime(LocalTime.MAX).format(DatePattern.NORM_DATETIME_FORMATTER);
        List<TimeDurationTableVo> exportData = dosCallCenterDndStatsDao.selectTimeDurationTable(startDateTime, endDateTime, null, dto.getQueueId());
        // 超时判断
        Map<String, String> agentStatusIdReminderMap = commonService.getAgentStatusIdReminderMap();
        for (TimeDurationTableVo vo : exportData) {
            // 判断当前状态是否超时
            String timeOut = agentStatusIdReminderMap.get(vo.getState());
            if (timeOut != null) {
                int stayDuration = vo.getDuration();
                if (stayDuration > Integer.parseInt(timeOut)) {
                    // tick表示超时，cross表示未超时
                    vo.setCurrentStateTimeOutFlag(CcBoardConstant.TICK);
                } else {
                    vo.setCurrentStateTimeOutFlag(CcBoardConstant.CROSS);
                }
            } else {
                vo.setCurrentStateTimeOutFlag(CcBoardConstant.CROSS);
            }
        }

        // 状态转换、队列转换
        Map<String, String> inzCountryMap = smsUtils.getDictList("CC_STATUS");
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(dto.getQueueId());
        Map<String, String> queueDeptMap = new HashMap<>(8);
        queueDeptList.forEach(queue -> queueDeptMap.put(queue.getQueueId(), queue.getQueueName()));
        if (!exportData.isEmpty()) {
            // 同坐席超时置顶、视图封装
            int i = 0, j = 0;
            String agentId = exportData.get(i).getAgentId();
            while (i < exportData.size()) {
                TimeDurationTableVo vo = exportData.get(i);
                if (agentId.equals(vo.getAgentId())) {
                    if (CcBoardConstant.TICK.equals(vo.getCurrentStateTimeOutFlag())) {
                        exportData.set(i, exportData.get(j));
                        exportData.set(j, vo);
                        j++;
                    }
                } else {
                    j = i;
                    agentId = vo.getAgentId();
                    continue;
                }
                vo.setState(inzCountryMap.get(vo.getState()));
                vo.setQueueName(queueDeptMap.get(vo.getQueueId()));
                i++;
            }
        }

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("agentName", "姓名");
        headerMap.put("agentId", "工号");
        headerMap.put("queueName", "队列名称");
        headerMap.put("state", "状态");
        headerMap.put("startDateTime", "开始时间");
        headerMap.put("endDateTime", "结束时间");
        headerMap.put("duration", "历时");
        headerMap.put("currentStateTimeOutFlag", "是否超时(✔超时/×未超时)");

        String sheetName = "坐席状态履历";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<TimeDurationTableVo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接入量图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    @Override
    public List<DateValueChartVo> accessNumberChart(DateDurationQueueIdDto dto) {
        List<DateValueChartVo> result = dosCallCenterRecordDao.selectAccessNumberGroupByDate(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        assembleDate(dto, result);
        return result;
    }

    /**
     * 接通率图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    @Override
    public List<DateValueChartVo> connectionRateChart(DateDurationQueueIdDto dto) {
        List<DateValueChartVo> result = dosCallCenterRecordDao.selectConnectionRateGroupByDate(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        assembleDate(dto, result);
        return result;
    }

    /**
     * 10s率图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    @Override
    public List<DateValueChartVo> tenSecondRateChart(DateDurationQueueIdDto dto) {
        List<DateValueChartVo> result = dosCallCenterRecordDao.selecttenSecondRateGroupByDate(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        assembleDate(dto, result);
        return result;
    }

    /**
     * 平均排队时间图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    @Override
    public List<DateValueChartVo> avgQueueDurationChart(DateDurationQueueIdDto dto) {
        List<DateValueChartVo> result = dosCallCenterRecordDao.selectAvgQueueDurationGroupByDate(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        assembleDate(dto, result);
        return result;
    }

    /**
     * 平均通话时长图表
     *
     * @param dto 查询条件
     * @return DateValueChartVo
     */
    @Override
    public List<DateValueChartVo> avgCallInDurationChart(DateDurationQueueIdDto dto) {
        List<DateValueChartVo> result = dosCallCenterRecordDao.selectAvgCallInDurationGroupByDate(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        assembleDate(dto, result);
        return result;
    }

    /**
     * 热线项目实时监控图表
     *
     * @param dto 查询条件
     * @return ProjectActualTimeMonitorChartVo
     */
    @Override
    public ProjectActualTimeMonitorChartVo projectActualTimeMonitorChart(DateDurationQueueIdDto dto) {
        return ProjectActualTimeMonitorChartVo.builder()
                .accessNumberChart(accessNumberChart(dto))
                .connectionRateChart(connectionRateChart(dto))
                .tenSecondRateChart(tenSecondRateChart(dto))
                .avgQueueDurationChart(avgQueueDurationChart(dto))
                .avgCallInDurationChart(avgCallInDurationChart(dto))
                .build();
    }

    /**
     * 人力数据表格
     *
     * @param dto 查询条件
     * @return List<HumanDataTableVo>
     */
    @Override
    public List<HumanDataTableVo> humanDataTable(DateDurationQueueIdDto dto) {
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        // 计算各时段总人力 = 员工 * 人力系数
        Map<LocalDateTime, Double> totalManpowerMap = getFrontLineManpowerMap(dto, localDateTimes);
        // null为true
        boolean frontLineManpowerFlag = totalManpowerMap == null;
        List<HumanDataTableBo> humanDataTableBoList = getHumanDataTable(dto);
        List<HumanDataTableVo> humanDataTableVoList = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            HumanDataTableBo humanDataTableBo = null;
            double totalManpower = frontLineManpowerFlag ? 0d : totalManpowerMap.get(localDateTime);
            LocalDateTime dbLocalDateTime = null;
            if (j < humanDataTableBoList.size()) {
                humanDataTableBo = humanDataTableBoList.get(j);
                dbLocalDateTime = humanDataTableBo.getTime();
            }
            String totalManpowerString = BigDecimal.valueOf(totalManpower).setScale(2, RoundingMode.HALF_UP).toString();
            if (humanDataTableBo == null || localDateTime.isBefore(dbLocalDateTime)) {
                i++;
                HumanDataTableVo build = HumanDataTableVo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .accessNumber("0")
                        .connectionNumber("0")
                        .connectionRate("0")
                        .tenSecondRate("0")
                        .totalManpower(totalManpowerString)
                        .build();
                humanDataTableVoList.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalDateTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalDateTime)) {
                HumanDataTableVo build = HumanDataTableVo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .accessNumber(humanDataTableBo.getAccessNumber())
                        .connectionNumber(humanDataTableBo.getConnectionNumber())
                        .connectionRate(humanDataTableBo.getConnectionRate())
                        .tenSecondRate(humanDataTableBo.getTenSecondRate())
                        .totalManpower(totalManpowerString)
                        .build();
                humanDataTableVoList.add(build);
                i++;
                j++;
            }
        }
        return humanDataTableVoList;
    }

    /**
     * 获取时段内人力数据表格
     *
     * @param dto 查询条件
     * @return HumanDataTableVo
     */
    private List<HumanDataTableBo> getHumanDataTable(DateDurationQueueIdDto dto) {
        return dosCallCenterRecordDao.selectHumanDataTable(dto);
    }

    /**
     * 时段在线及工时表格导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void timeSlotOnlineWorkingHourTableExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        DateDurationQueueIdPageDto buildDto = DateDurationQueueIdPageDto.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .queueId(dto.getQueueId())
                .build();
        List<TimeSlotOnlineWorkingHourTableVo> exportData = getTimeSlotOnlineWorkingHourTable(buildDto);
        // 全天工作时间工作标记
        List<String> agentIdList = new ArrayList<>(exportData.size());
        exportData.forEach(vo -> agentIdList.add(vo.getAgentId()));
        Map<String, List<TimeDurationVo>> agentTimeDurationFlagMap = getAgentTimeDurationFlag(buildDto, agentIdList, 2);

        // 根据agentId连接表格字段
        for (TimeSlotOnlineWorkingHourTableVo vo : exportData) {
            String agentId = vo.getAgentId();
            List<TimeDurationVo> timeDurationVoList = agentTimeDurationFlagMap.get(agentId);
            vo.setTimeDuration(timeDurationVoList);
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        String sheetName = "时段在线及工时表格";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = null;

        // 设置表头
        int columnIndex = 0;
        row = sheet.createRow(0);
        row.createCell(columnIndex).setCellValue("姓名");
        row.createCell(++columnIndex).setCellValue("工号");
        row.createCell(++columnIndex).setCellValue("接入量");
        row.createCell(++columnIndex).setCellValue("单位小时接入量");
        row.createCell(++columnIndex).setCellValue("工时利用率");
        List<LocalDateTime> intervalList = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        for (LocalDateTime start : intervalList) {
            row.createCell(++columnIndex).setCellValue(start.format(DatePattern.NORM_DATETIME_FORMATTER));
        }

        // 数据填充
        for (int i = 0; i < exportData.size(); i++) {
            row = sheet.createRow(i + 1);
            columnIndex = 0;

            TimeSlotOnlineWorkingHourTableVo vo = exportData.get(i);
            row.createCell(columnIndex).setCellValue(vo.getAgentName());
            row.createCell(++columnIndex).setCellValue(vo.getAgentId());
            row.createCell(++columnIndex).setCellValue(vo.getAccessNumber());
            row.createCell(++columnIndex).setCellValue(vo.getHourlyAccessNumber());
            row.createCell(++columnIndex).setCellValue(vo.getManHourUtilizationRate());
            List<TimeDurationVo> timeDuration = vo.getTimeDuration();
            for (TimeDurationVo timeDurationVo : timeDuration) {
                row.createCell(++columnIndex).setCellValue(timeDurationVo.getFlag() == 1 ? CcBoardConstant.TICK : CcBoardConstant.CROSS);
            }
        }

        //列宽自适应
        for (int i = 0; i <= columnIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/msexcel;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BusinessException("导出Excel失败");
        }
    }

    /**
     * 坐席状态表格导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void agentStateTableExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        StateDateDurationQueueIdPageDto buildDto = StateDateDurationQueueIdPageDto.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .queueId(dto.getQueueId())
                .state(dto.getState())
                .build();
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<String> serviceStatusList = getCcWorkStatus(CcBoardConstant.CC_SERVICE_STATUS);
        List<AgentStateTableVo> exportData = dosCallCenterRecordDao.selectAgentStateTable(buildDto, statusList, serviceStatusList, 1);

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("agentName", "姓名");
        headerMap.put("agentId", "工号");
        headerMap.put("connectNumber", "接通量");
        headerMap.put("hourlyConnectNumber", "单位小时接通量");
        headerMap.put("manHourUtilizationRate", "工时利用率");
        headerMap.put("leisureCumulativeDuration", "累积示闲时长");
        headerMap.put("restCumulativeDuration", "累积小休时长");
        headerMap.put("afterCallCumulativeDuration", "累积话后时长");
        headerMap.put("busyCumulativeDuration", "累积示忙时长");

        String sheetName = "坐席状态表格";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<AgentStateTableVo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 10s接通率表格导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void tenSecondRateTableExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        List<String> queueIdList = dto.getQueueId();
        queueIdList = queueIdList.stream().sorted().collect(Collectors.toList());
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(queueIdList);
        DateDurationQueueIdPageDto buildDto = DateDurationQueueIdPageDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build();
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<TenSecondRateTableVo> dbDataList = dosCallCenterRecordDao.selectTenSecondRateTable(buildDto, statusList);
        // queueId关联
        List<TenSecondRateTableVo> exportData = new ArrayList<>(queueDeptList.size());
        int i = 0, j = 0;
        while (i < queueDeptList.size()) {
            CcQueueDept ccQueueDept = queueDeptList.get(i);
            TenSecondRateTableVo dbData = null;
            if (j < dbDataList.size()) {
                dbData = dbDataList.get(j);
            }
            if (dbData == null || ccQueueDept.getQueueId().compareTo(dbData.getQueueId()) < 0) {
                i++;
                TenSecondRateTableVo buildVo = TenSecondRateTableVo.builder()
                        .queueId(ccQueueDept.getQueueId())
                        .queueName(ccQueueDept.getQueueName())
                        .build();
                exportData.add(buildVo);
                continue;
            }
            if (ccQueueDept.getQueueId().compareTo(dbData.getQueueId()) > 0) {
                j++;
                continue;
            }
            if (ccQueueDept.getQueueId().equals(dbData.getQueueId())) {
                String queueName = ccQueueDept.getQueueName();
                dbData.setQueueName(queueName);
                exportData.add(dbData);
                i++;
                j++;
            }
        }

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("queueName", "队列名称");
        headerMap.put("accessNumber", "接入量");
        headerMap.put("connectNumber", "接通量");
        headerMap.put("connectionRate", "接通率");
        headerMap.put("tenSecondRate", "10s接通率");
        headerMap.put("avgQueueAgent", "技能组平均坐席");
        headerMap.put("avgCallInDuration", "呼入平均通话时间");
        headerMap.put("manHourUtilizationRate", "工时利用率");
        headerMap.put("callManHourUtilizationRate", "通话工时利用率");

        String sheetName = "10s接通率表格";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<TenSecondRateTableVo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实时排队导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void realTimeQueueChartExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        // 查询所在部门下的所有队列
        List<CcQueueDept> totalSubQueueDeptList = ccBaseConfigDao.selectSubQueueIdByQueueId(dto.getQueueId());
        // 查询 所选队列信息-用于补充图例
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(dto.getQueueId());
        DateDurationQueueIdDto buildDto = DateDurationQueueIdDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build();
        // 查询队列历史实时排队数据
        List<QueueMonitorBo> monitorBoList = dosCallCenterQueueMonitorDao.selectHistoryRealTimeQueueChart(buildDto);

        // 查询队列所在部门的汇总数据
        String deptId = totalSubQueueDeptList.get(0).getDeptId();
        List<String> queueIdList = new ArrayList<>(6);
        int i = 0;
        while (i < totalSubQueueDeptList.size()) {
            CcQueueDept ccQueueDept = totalSubQueueDeptList.get(i);
            if (deptId.equals(ccQueueDept.getDeptId())) {
                queueIdList.add(ccQueueDept.getQueueId());
                i++;
            } else {
                dto.setQueueId(queueIdList);
                List<QueueMonitorBo> parentRealTimeQueueChartList = dosCallCenterQueueMonitorDao.selectHistoryParentRealTimeQueueChart(buildDto, deptId);
                monitorBoList.addAll(parentRealTimeQueueChartList);
                deptId = ccQueueDept.getDeptId();
                queueIdList.clear();
            }
        }
        // 最后一次查询
        dto.setQueueId(queueIdList);
        List<QueueMonitorBo> parentRealTimeQueueChartList = dosCallCenterQueueMonitorDao.selectHistoryParentRealTimeQueueChart(buildDto, deptId);
        monitorBoList.addAll(parentRealTimeQueueChartList);
        monitorBoList.sort(Comparator.comparing(QueueMonitorBo::getTime));

        // 所选队列所在部门是否为全部队列
        Map<String, Boolean> queueIsAllBelongDeptFlagMap = getQueueIsAllBelongDeptFlagMap(totalSubQueueDeptList, queueDeptList);

        // 封装视图
        List<RealTimeQueueChartVo> exportData = new ArrayList<>(10);
        ArrayList<RealTimeQueueVo> queueVoList = new ArrayList<>(10);
        i = 0;
        if (!monitorBoList.isEmpty()) {
            LocalDateTime commonTime = monitorBoList.get(i).getTime();
            while (i < monitorBoList.size()) {
                QueueMonitorBo queueMonitorBo = monitorBoList.get(i);
                if (commonTime.equals(queueMonitorBo.getTime())) {
                    // 先查询队列名称
                    CcQueueDept ccQueueDept = totalSubQueueDeptList.stream().filter(dept -> dept.getQueueId().equals(queueMonitorBo.getQueueNumber())).findFirst().orElse(null);
                    String queueName = null;
                    if (ccQueueDept == null) {
                        // 查询不到队列名称，再查询部门名称
                        ccQueueDept = totalSubQueueDeptList.stream().filter(dept -> dept.getDeptId().equals(queueMonitorBo.getQueueNumber())).findFirst().orElse(null);
                        queueName = ccQueueDept != null ? ccQueueDept.getDeptName() : null;
                    } else {
                        // 提前判断是否选择全部部门
                        if (queueIsAllBelongDeptFlagMap.get(ccQueueDept.getDeptId())) {
                            i++;
                            continue;
                        }
                        queueName = ccQueueDept.getQueueName();
                    }
                    RealTimeQueueVo realTimeQueueVo = RealTimeQueueVo.builder().id(queueMonitorBo.getQueueNumber()).name(queueName).count(queueMonitorBo.getCurrentWaitNumber()).build();
                    queueVoList.add(realTimeQueueVo);
                    i++;
                } else {
                    ArrayList<RealTimeQueueVo> tempQueue = supplementLegend(queueIsAllBelongDeptFlagMap, queueDeptList, queueVoList);
                    RealTimeQueueChartVo buildVo = RealTimeQueueChartVo.builder().time(commonTime.format(DatePattern.NORM_DATETIME_FORMATTER)).queue(tempQueue).build();
                    exportData.add(buildVo);
                    commonTime = queueMonitorBo.getTime();
                    queueVoList.clear();
                }
            }
            ArrayList<RealTimeQueueVo> tempQueue = supplementLegend(queueIsAllBelongDeptFlagMap, queueDeptList, queueVoList);
            RealTimeQueueChartVo buildVo = RealTimeQueueChartVo.builder().time(commonTime.format(DatePattern.NORM_DATETIME_FORMATTER)).queue(tempQueue).build();
            exportData.add(buildVo);
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        String sheetName = "实时排队";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = null;

        // 设置表头
        int columnIndex = 0;
        row = sheet.createRow(0);
        row.createCell(columnIndex).setCellValue("时间");
        for (RealTimeQueueChartVo exportDatum : exportData) {
            for (RealTimeQueueVo realTimeQueueVo : exportDatum.getQueue()) {
                row.createCell(++columnIndex).setCellValue(realTimeQueueVo.getName() + CcChartConstant.CURRENT_WAIT_NUMBER);
            }
            break;
        }

        // 数据填充
        for (int j = 0; j < exportData.size(); j++) {
            row = sheet.createRow(j + 1);
            columnIndex = 0;

            RealTimeQueueChartVo vo = exportData.get(j);
            row.createCell(columnIndex).setCellValue(vo.getTime());
            List<RealTimeQueueVo> queueList = vo.getQueue();
            for (RealTimeQueueVo realTimeQueueVo : queueList) {
                row.createCell(++columnIndex).setCellValue(realTimeQueueVo.getCount());
            }
        }

        //列宽自适应
        for (int j = 0; j <= columnIndex; j++) {
            sheet.autoSizeColumn(j);
        }

        response.setContentType("application/msexcel;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new BusinessException("导出Excel失败");
        }
    }

    /**
     * 坐席工时利用率导出
     *
     * @param response response
     * @param dto      查询条件
     */
    @Override
    public void agentManHourUtilizationRateChartExport(HttpServletResponse response, HotLineExportConditionDto dto, String callType) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        List<AgentManHourUtilizationRateChartBo> exportData = new ArrayList<>(localDateTimes.size());
        DateDurationQueueIdDto buildDto = DateDurationQueueIdDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build();
        // 单位小时接入量/呼出量
        List<IntervalCallDurationRingTimeHourlyCallNumberBo> intervalCallDurationRingTimeHourlyCallNumberBoList = dosCallCenterRecordDao.selectIntervalCallDurationRingTimeBo(buildDto, callType);
        // 通话时长、振铃时长、空闲时长、话后时长、忙碌时长、培训时长
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<StartEndDateTimeDurationBo> otherStateBoList = dosCallCenterDndStatsDao.selectStartEndDateTimeDuration(buildDto, callType, statusList);
        // 登录时长
        List<StartEndDateTimeDurationBo> loginBoList = dosCallCenterCheckStatsDao.selectStartEndDateTimeDuration(buildDto);
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime intervalStart = localDateTimes.get(i);
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            IntervalCallDurationRingTimeHourlyCallNumberBo intervalCallDurationRingTimeBo = null;
            LocalDateTime dbLocalDateTime = null;
            int molecule = 0, denominator = 0;
            int hourlyCallNumber = 0;
            String manHourUtilizationRate;
            if (j < intervalCallDurationRingTimeHourlyCallNumberBoList.size()) {
                intervalCallDurationRingTimeBo = intervalCallDurationRingTimeHourlyCallNumberBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(intervalCallDurationRingTimeBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }

            // 分子累加通话时长、振铃时长，获取单位小时接入量/呼出量
            if (dbLocalDateTime != null && intervalStart.isEqual(dbLocalDateTime)) {
                hourlyCallNumber = intervalCallDurationRingTimeBo.getHourlyCallNumber();
                j++;
            }
            // 分子累加通话时长、振铃时长、空闲时长、话后时长、忙碌时长、培训时长
            molecule += getIntervalStateDuration(intervalStart, intervalEnd, otherStateBoList);
            // 分母累加登录时长
            denominator += getIntervalStateDuration(intervalStart, intervalEnd, loginBoList);
            // 工时利用率
            manHourUtilizationRate = denominator == 0 || (molecule > denominator) ? String.valueOf(100) : BigDecimal.valueOf(molecule).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP).toString();
            AgentManHourUtilizationRateChartBo build = AgentManHourUtilizationRateChartBo.builder()
                    .time(intervalStart.format(DatePattern.NORM_DATETIME_FORMATTER))
                    .hourlyCallNumber(String.valueOf(hourlyCallNumber))
                    .manHourUtilizationRate(manHourUtilizationRate)
                    .build();
            exportData.add(build);
            i++;
        }

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("time", "时间");
        headerMap.put("hourlyCallNumber", callType.equals(CcBoardConstant.IN) ? "单位小时接入量" : "单位小时完成量");
        headerMap.put("manHourUtilizationRate", "工时利用率");

        String sheetName = "坐席工时利用率图表";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<AgentManHourUtilizationRateChartBo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 量级数据导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void magnitudeDataChartExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        // 获取一线人力队列id（写死）
        List<String> frontLineManpowerQueueId = commonService.getFrontLineManpowerQueueId();
        // 入参队列与一线队列求交集
        dto.setQueueId(DataUtil.getIntersection(dto.getQueueId(), frontLineManpowerQueueId));

        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        DateDurationQueueIdDto buildDto = DateDurationQueueIdDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build();
        List<MagnitudeDataChartBo> magnitudeDataChartBoList = dosCallCenterRecordDao.selectMagnitudeDataChart(buildDto);
        // 计算各时段一线人力 = 员工 * 人力系数
        Map<LocalDateTime, Double> frontLineManpowerMap = getFrontLineManpowerMap(buildDto, localDateTimes);
        // 根据时间拼接数据
        List<MagnitudeDataChartBo> exportData = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            MagnitudeDataChartBo magnitudeDataChartBo = null;
            Double frontLineManpower = frontLineManpowerMap.get(localDateTime);
            Double theoryConnectNumber = BigDecimal.valueOf(frontLineManpower).multiply(BigDecimal.valueOf(CcBoardConstant.FRONT_LINE_MANPOWER_MULTI_VALUE)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            Double frontLineManpowerDouble = BigDecimal.valueOf(frontLineManpower).setScale(2, RoundingMode.HALF_UP).doubleValue();
            LocalDateTime dbLocalDateTime = null;
            if (j < magnitudeDataChartBoList.size()) {
                magnitudeDataChartBo = magnitudeDataChartBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(magnitudeDataChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            if (magnitudeDataChartBo == null || localDateTime.isBefore(dbLocalDateTime)) {
                i++;
                MagnitudeDataChartBo build = MagnitudeDataChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .theoryConnectNumber(theoryConnectNumber)
                        .connectNumber(0)
                        .accessNumber(0)
                        .frontLineManpower(frontLineManpowerDouble)
                        .tenSecondRate(String.valueOf(0))
                        .build();
                exportData.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalDateTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalDateTime)) {
                MagnitudeDataChartBo build = MagnitudeDataChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .theoryConnectNumber(theoryConnectNumber)
                        .connectNumber(Optional.ofNullable(magnitudeDataChartBo.getConnectNumber()).orElse(0))
                        .accessNumber(Optional.ofNullable(magnitudeDataChartBo.getAccessNumber()).orElse(0))
                        .frontLineManpower(frontLineManpowerDouble)
                        .tenSecondRate(Optional.ofNullable(magnitudeDataChartBo.getTenSecondRate()).orElse(String.valueOf(0)))
                        .build();
                exportData.add(build);
                i++;
                j++;
            }
        }

        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("time", "时间");
        headerMap.put("theoryConnectNumber", "理论接起量");
        headerMap.put("connectNumber", "接起量");
        headerMap.put("accessNumber", "进线量");
        headerMap.put("frontLineManpower", "一线人力");
        headerMap.put("tenSecondRate", "10接通率");
        String sheetName = "量级数据图表";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<MagnitudeDataChartBo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进线统计导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void incomeStatisticChartExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        List<IncomeStatisticChartBo> incomeStatisticChartBoList = dosCallCenterRecordDao.selectIncomeStatisticChart(DateDurationQueueIdDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build());
        List<IncomeStatisticChartBo> exportData = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            IncomeStatisticChartBo incomeStatisticChartBo = null;
            LocalDateTime dbLocalTime = null;
            if (j < incomeStatisticChartBoList.size()) {
                incomeStatisticChartBo = incomeStatisticChartBoList.get(j);
                dbLocalTime = LocalDateTime.parse(incomeStatisticChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            if (incomeStatisticChartBo == null || localDateTime.isBefore(dbLocalTime)) {
                i++;
                IncomeStatisticChartBo build = IncomeStatisticChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .accessNumber(0)
                        .connectNumber(0)
                        .connectionRate(String.valueOf(0.00))
                        .build();
                exportData.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalTime)) {
                IncomeStatisticChartBo build = IncomeStatisticChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .accessNumber(incomeStatisticChartBo.getAccessNumber())
                        .connectNumber(incomeStatisticChartBo.getConnectNumber())
                        .connectionRate(incomeStatisticChartBo.getConnectionRate())
                        .build();
                exportData.add(build);
                i++;
                j++;
            }
        }
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("time", "时间");
        headerMap.put("accessNumber", "进线量");
        headerMap.put("connectNumber", "接通量");
        headerMap.put("connectionRate", "接通率");
        String sheetName = "进线统计图表";
        String fileName = sheetName + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<IncomeStatisticChartBo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 人力数据导出
     *
     * @param response response
     * @param dto      查询条件
     */
    private void humanDataChartExport(HttpServletResponse response, HotLineExportConditionDto dto) {
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        DateDurationQueueIdDto buildDto = DateDurationQueueIdDto.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).queueId(dto.getQueueId()).build();
        List<HumanDataChartBo> humanDataChartBoList = getHumanDataChartBo(buildDto);
        List<CheckInAgentNumberIntervalBo> checkInAgentNumberIntervalBoList = getCheckInAgentNumberInterval(buildDto, localDateTimes);
        // 每月核心目标
        Map<String, String> targetMap = getMonthTarget(dto.getStartDate(), dto.getEndDate());

        List<HumanDataChartBo> exportData = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localDateTime = localDateTimes.get(i);
            CheckInAgentNumberIntervalBo checkInAgentNumberIntervalBo = checkInAgentNumberIntervalBoList.get(i);
            HumanDataChartBo humanDataChartBo = null;
            LocalDateTime dbLocalDateTime = null;
            if (j < humanDataChartBoList.size()) {
                humanDataChartBo = humanDataChartBoList.get(j);
                dbLocalDateTime = LocalDateTime.parse(humanDataChartBo.getTime(), DatePattern.NORM_DATETIME_FORMATTER);
            }
            if (humanDataChartBo == null || localDateTime.isBefore(dbLocalDateTime)) {
                i++;
                HumanDataChartBo build = HumanDataChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .checkInAgentNumber(checkInAgentNumberIntervalBo.getCheckInAgentNumber())
                        .tenSecondRate(String.valueOf(0))
                        .accessNumber(0)
                        .tenSecondRateTarget(targetMap.get(localDateTime.format(DatePattern.NORM_MONTH_FORMATTER)))
                        .build();
                exportData.add(build);
                continue;
            }
            if (localDateTime.isAfter(dbLocalDateTime)) {
                j++;
                continue;
            }
            if (localDateTime.equals(dbLocalDateTime)) {
                HumanDataChartBo build = HumanDataChartBo.builder()
                        .time(localDateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .checkInAgentNumber(checkInAgentNumberIntervalBo.getCheckInAgentNumber())
                        .tenSecondRate(Optional.ofNullable(humanDataChartBo.getTenSecondRate()).orElse(String.valueOf(0)))
                        .accessNumber(Optional.ofNullable(humanDataChartBo.getAccessNumber()).orElse(0))
                        .tenSecondRateTarget(targetMap.get(localDateTime.format(DatePattern.NORM_MONTH_FORMATTER)))
                        .build();
                exportData.add(build);
                i++;
                j++;
            }
        }
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("time", "时间");
        headerMap.put("checkInAgentNumber", "在线坐席");
        headerMap.put("tenSecondRate", "10s接通率");
        headerMap.put("tenSecondRateTarget", "目标10s接通率");
        headerMap.put("accessNumber", "进线量");
        String sheetName = "人力数据图表";
        String fileName = "人力数据图表" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xls";
        try {
            ExcelTool<HumanDataChartBo> excelTool = new ExcelTool<>(fileName, sheetName);
            List<Column> titleData = excelTool.columnTransformer(headerMap);
            excelTool.exportWorkbook(fileName, response, titleData, exportData, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取每月核心目标
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return key:yyyy-MM value:目标值
     */
    private Map<String, String> getMonthTarget(String startDate, String endDate) {
        Map<String, String> targetMap = new HashMap<>(4);
        LocalDate startLocalDate = LocalDate.parse(startDate).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate endLocalDate = LocalDate.parse(endDate).with(TemporalAdjusters.lastDayOfMonth());
        List<String> yearMonthList = new ArrayList<>(4);
        for (int i = 0; i <= Period.between(startLocalDate, endLocalDate).getMonths(); i++) {
            String yearMonth = startLocalDate.plusMonths(i).format(DatePattern.NORM_MONTH_FORMATTER);
            yearMonthList.add(yearMonth);
        }
        List<CcCoreTarget> coreTargetList = ccBaseConfigDao.selectBatchConnRateByTargetMonth(yearMonthList);
        coreTargetList.forEach(target -> targetMap.put(target.getTargetMonth(), target.getConnRate()));
        return targetMap;
    }

    /**
     * 坐席状态表格-状态历史表格
     *
     * @param dto 查询条件
     * @return PageInfo<AgentStateHistoryTableVo>
     */
    @Override
    public PageInfo<TimeDurationTableVo> agentStateHistoryTable(AgentIdDateDurationPageDto dto) {
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(AgentIdDateDurationPageDto.class, dto);
        String start = LocalDate.parse(dto.getStartDate()).atTime(LocalTime.MIN).format(DatePattern.NORM_DATETIME_FORMATTER);
        String end = LocalDate.parse(dto.getEndDate()).atTime(LocalTime.MAX).format(DatePattern.NORM_DATETIME_FORMATTER);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<TimeDurationTableVo> timeDurationTableVoList = dosCallCenterDndStatsDao.selectTimeDurationTable(start, end, dto.getAgentId(), null);
        Map<String, String> inzCountryMap = smsUtils.getDictList("CC_STATUS");
        timeDurationTableVoList.forEach(vo -> vo.setState(inzCountryMap.get(vo.getState())));
        return new PageInfo<>(timeDurationTableVoList);
    }

    /**
     * 在线坐席
     *
     * @param dto 查询条件
     * @return List<OnlineAgentVo>
     */
    @Override
    public OnlineAgentVo onlineAgent(QueueIdDto dto) {
        OnlineAgentBo onlineAgentBo = dosCallCenterQueueMonitorDao.selectOnlineAgentByQueueId(dto);

        List<AgentStateNumberVo> agentStateNumberVoList = new ArrayList<>(5);
        agentStateNumberVoList.add(AgentStateNumberVo.builder().state("示闲").number(onlineAgentBo.getFreeAgents()).overtimeNumber(onlineAgentBo.getFreeTimeOutAgents()).build());
        agentStateNumberVoList.add(AgentStateNumberVo.builder().state("话后处理").number(onlineAgentBo.getAfterAgents()).overtimeNumber(onlineAgentBo.getAfterTimeOutAgents()).build());
        agentStateNumberVoList.add(AgentStateNumberVo.builder().state("小休").number(onlineAgentBo.getRestAgents()).overtimeNumber(onlineAgentBo.getRestTimeOutAgents()).build());
        agentStateNumberVoList.add(AgentStateNumberVo.builder().state("示忙").number(onlineAgentBo.getBusyAgents()).overtimeNumber(onlineAgentBo.getBusyTimeOutAgents()).build());
        agentStateNumberVoList.add(AgentStateNumberVo.builder().state("通话中").number(onlineAgentBo.getOnCallAgents()).overtimeNumber(null).build());

        return OnlineAgentVo.builder().checkInAgents(onlineAgentBo.getCheckInAgents()).agentState(agentStateNumberVoList).build();
    }

    /**
     * 实时排队图表
     *
     * @param dto 查询条件
     * @return List<RealTimeQueueChartVo>
     */
    @Override
    public List<RealTimeQueueChartVo> realTimeQueueChart(DateDurationQueueIdDto dto) {
        String key = RedisKeyConstant.REAL_TIME_QUEUE_CHART + dto.getQueueId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (List<RealTimeQueueChartVo>) redisTemplate.opsForValue().get(key);
        }
        // 页面不支持跨天查询
        DataUtil.limitCrossDayQuery(DateDurationQueueIdDto.class, dto);
        // 查询所在部门下的所有队列
        List<CcQueueDept> totalSubQueueDeptList = ccBaseConfigDao.selectSubQueueIdByQueueId(dto.getQueueId());
        // 查询 所选队列信息-用于补充图例
        List<CcQueueDept> queueDeptList = ccBaseConfigDao.selectQueueDeptInfoByQueueId(dto.getQueueId());
        // 仅查询上班期间数据
        String[] workTime = commonService.getStartWorkTime();
        LocalTime startTime = LocalTime.parse(workTime[0], DatePatternUtil.NORM_MINUTE_FORMATTER);
        LocalTime endTime = LocalTime.parse(workTime[1], DatePatternUtil.NORM_MINUTE_FORMATTER);
        String originalStartDate = dto.getStartDate();
        String originalEndDate = dto.getEndDate();
        dto.setStartDate(LocalDate.parse(originalStartDate).atTime(startTime).toString());
        dto.setEndDate(LocalDate.parse(originalEndDate).atTime(endTime).toString());
        // 查询队列实时排队数据
        List<QueueMonitorBo> monitorBoList = dosCallCenterQueueMonitorDao.selectRealTimeQueueChart(dto);

        // 查询队列所在部门的汇总数据
        String deptId = totalSubQueueDeptList.get(0).getDeptId();
        List<String> queueIdList = new ArrayList<>(6);
        int i = 0;
        while (i < totalSubQueueDeptList.size()) {
            CcQueueDept ccQueueDept = totalSubQueueDeptList.get(i);
            if (deptId.equals(ccQueueDept.getDeptId())) {
                queueIdList.add(ccQueueDept.getQueueId());
                i++;
            } else {
                dto.setQueueId(queueIdList);
                List<QueueMonitorBo> parentRealTimeQueueChartList = dosCallCenterQueueMonitorDao.selectParentRealTimeQueueChart(dto, deptId);
                monitorBoList.addAll(parentRealTimeQueueChartList);
                deptId = ccQueueDept.getDeptId();
                queueIdList.clear();
            }
        }
        // 最后一次查询
        dto.setQueueId(queueIdList);
        List<QueueMonitorBo> parentRealTimeQueueChartList = dosCallCenterQueueMonitorDao.selectParentRealTimeQueueChart(dto, deptId);
        monitorBoList.addAll(parentRealTimeQueueChartList);
        monitorBoList.sort(Comparator.comparing(QueueMonitorBo::getTime));

        // 所选队列所在部门是否为全部队列
        Map<String, Boolean> queueIsAllBelongDeptFlagMap = getQueueIsAllBelongDeptFlagMap(totalSubQueueDeptList, queueDeptList);

        // 封装视图
        List<RealTimeQueueChartVo> result = new ArrayList<>(10);
        ArrayList<RealTimeQueueVo> queueVoList = new ArrayList<>(10);
        i = 0;
        if (monitorBoList.isEmpty()) {
            List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(originalStartDate), LocalDate.parse(originalEndDate), ChronoUnit.SECONDS, 5);
            for (LocalDateTime localDateTime : localDateTimes) {
                ArrayList<RealTimeQueueVo> tempQueue = supplementLegend(queueIsAllBelongDeptFlagMap, queueDeptList, queueVoList);
                result.add(RealTimeQueueChartVo.builder().time(localDateTime.toLocalTime().toString()).queue(tempQueue).build());
            }
            return result;
        }
        LocalDateTime commonTime = monitorBoList.get(i).getTime();
        while (i < monitorBoList.size()) {
            QueueMonitorBo queueMonitorBo = monitorBoList.get(i);
            if (commonTime.equals(queueMonitorBo.getTime())) {
                // 先查询队列名称
                CcQueueDept ccQueueDept = totalSubQueueDeptList.stream().filter(dept -> dept.getQueueId().equals(queueMonitorBo.getQueueNumber())).findFirst().orElse(null);
                String queueName = null;
                if (ccQueueDept == null) {
                    // 查询不到队列名称，再查询部门名称
                    ccQueueDept = totalSubQueueDeptList.stream().filter(dept -> dept.getDeptId().equals(queueMonitorBo.getQueueNumber())).findFirst().orElse(null);
                    queueName = ccQueueDept != null ? ccQueueDept.getDeptName() : null;
                } else {
                    // 提前判断是否选择全部部门
                    if (queueIsAllBelongDeptFlagMap.get(ccQueueDept.getDeptId())) {
                        i++;
                        continue;
                    }
                    queueName = ccQueueDept.getQueueName();
                }
                RealTimeQueueVo realTimeQueueVo = RealTimeQueueVo.builder().id(queueMonitorBo.getQueueNumber()).name(queueName).count(queueMonitorBo.getCurrentWaitNumber()).build();
                queueVoList.add(realTimeQueueVo);
                i++;
            } else {
                ArrayList<RealTimeQueueVo> tempQueue = supplementLegend(queueIsAllBelongDeptFlagMap, queueDeptList, queueVoList);
                RealTimeQueueChartVo buildVo = RealTimeQueueChartVo.builder().time(commonTime.format(DatePattern.NORM_TIME_FORMATTER)).queue(tempQueue).build();
                result.add(buildVo);
                commonTime = queueMonitorBo.getTime();
                queueVoList.clear();
            }
        }
        ArrayList<RealTimeQueueVo> tempQueue = supplementLegend(queueIsAllBelongDeptFlagMap, queueDeptList, queueVoList);
        RealTimeQueueChartVo buildVo = RealTimeQueueChartVo.builder().time(commonTime.format(DatePattern.NORM_TIME_FORMATTER)).queue(tempQueue).build();
        result.add(buildVo);

        Map<String, String> timeoutMap = smsUtils.getDictList("CC_REAL_TIME_QUEUE_REDIS_TIMEOUT");
        long timeout = 20;
        for (Map.Entry<String, String> entry : timeoutMap.entrySet()) {
            timeout = Long.parseLong(entry.getKey());
        }
        synchronized (redisTemplate) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return result;
            }
            redisTemplate.opsForValue().set(key, result, timeout, TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 所选队列所在部门是否为全部队列
     *
     * @param totalSubQueueDeptList 部门下所有队列信息
     * @param queueDeptList         所选队列信息
     * @return key:deptId value:true选择了全部部门；false没选择全部部门；
     */
    private Map<String, Boolean> getQueueIsAllBelongDeptFlagMap(List<CcQueueDept> totalSubQueueDeptList, List<CcQueueDept> queueDeptList) {
        Map<String, Boolean> queueIsAllBelongDeptFlagMap = new HashMap<>(2);
        // 部门下全部队列数
        Map<String, Long> deptTotalQueueCountMap = totalSubQueueDeptList.stream().collect(Collectors.groupingBy(CcQueueDept::getDeptId, Collectors.counting()));
        // 部门下所选队列数
        Map<String, Long> deptSelectQueueCountMap = queueDeptList.stream().collect(Collectors.groupingBy(CcQueueDept::getDeptId, Collectors.counting()));
        for (Map.Entry<String, Long> entry : deptSelectQueueCountMap.entrySet()) {
            if (entry.getValue().equals(deptTotalQueueCountMap.getOrDefault(entry.getKey(), 0L))) {
                queueIsAllBelongDeptFlagMap.put(entry.getKey(), true);
            } else {
                queueIsAllBelongDeptFlagMap.put(entry.getKey(), false);
            }
        }
        return queueIsAllBelongDeptFlagMap;
    }

    /**
     * 1.补充图例，无数据补 0；
     * 2.所选队列等于所在部门下所有队列，则只显示所在部门汇总数据；
     *
     * @param queueIsAllBelongDeptFlagMap 所选队列所在部门是否为全部队列
     * @param queueDeptList               被查询队列的队列信息
     * @param queueVoList                 视图
     * @return 补充后的视图
     */
    private ArrayList<RealTimeQueueVo> supplementLegend(Map<String, Boolean> queueIsAllBelongDeptFlagMap, List<CcQueueDept> queueDeptList, ArrayList<RealTimeQueueVo> queueVoList) {
        for (Map.Entry<String, Boolean> entry : queueIsAllBelongDeptFlagMap.entrySet()) {
            // 选择全部队列显示部门汇总数据，不存在则补 0
            if (queueVoList.stream().noneMatch(vo -> vo.getId().equals(entry.getKey()))) {
                String deptName = null;
                for (CcQueueDept ccQueueDept : queueDeptList) {
                    if (entry.getKey().equals(ccQueueDept.getDeptId())) {
                        deptName = ccQueueDept.getDeptName();
                        break;
                    }
                }
                queueVoList.add(RealTimeQueueVo.builder().id(entry.getKey()).name(deptName).count(0).build());
            }
        }
        for (CcQueueDept ccQueueDept : queueDeptList) {
            // 判断是否选择全部队列
            if (queueIsAllBelongDeptFlagMap.getOrDefault(ccQueueDept.getDeptId(), true)) {
                continue;
            }
            // 队列判断
            if (queueVoList.stream().noneMatch(vo -> vo.getName().equals(ccQueueDept.getQueueName()))) {
                queueVoList.add(RealTimeQueueVo.builder().id(ccQueueDept.getQueueId()).name(ccQueueDept.getQueueName()).count(0).build());
            }
        }
        return (ArrayList<RealTimeQueueVo>) queueVoList.clone();
    }

    /**
     * 热线呼叫业务监控
     *
     * @param dto 查询条件
     * @return List<AxisVo>
     */
    @Override
    public List<CallServiceMonitorAxisVo> callServiceMonitor(QueueIdDto dto) {
        String localDate = LocalDate.now().toString();
        DateDurationQueueIdDto buildDto = DateDurationQueueIdDto.builder().startDate(localDate).endDate(localDate).queueId(dto.getQueueId()).build();
        CallServiceMonitorBo monitorBo = dosCallCenterQueueMonitorDao.selectCallServiceMonitor(buildDto);
        List<CallServiceMonitorAxisVo> result = new ArrayList<>(5);
        result.add(CallServiceMonitorAxisVo.builder().nodeName("来电").nodeNumber(Optional.ofNullable(monitorBo.getTotalEnterAcdOfNow()).orElse(0)).build());
        result.add(CallServiceMonitorAxisVo.builder().nodeName("ACD排队").nodeNumber(Optional.ofNullable(monitorBo.getCurrentWaitNumber()).orElse(0)).watingNumber(Optional.ofNullable(monitorBo.getCurrentWaitNumber()).orElse(0)).build());
        result.add(CallServiceMonitorAxisVo.builder().nodeName("分配坐席").stateTime(Optional.ofNullable(monitorBo.getAvgCurrentAcdDuration()).orElse(0d)).nodeNumber(Optional.ofNullable(monitorBo.getAllocateAgentNumber()).orElse(0)).build());
        result.add(CallServiceMonitorAxisVo.builder().nodeName("坐席接起").stateTime(Optional.ofNullable(monitorBo.getAvgCurrentRingDuration()).orElse(0d)).timeOutNumber(Optional.ofNullable(monitorBo.getExceedTenSecondConnection()).orElse(0)).nodeNumber(Optional.ofNullable(monitorBo.getOnCallAgents()).orElse(0)).build());
        result.add(CallServiceMonitorAxisVo.builder().nodeName("话后处理").stateTime(Optional.ofNullable(monitorBo.getAvgCurrentBillingSeconds()).orElse(0d)).nodeNumber(Optional.ofNullable(monitorBo.getAfterAgents()).orElse(0)).timeOutNumber(Optional.ofNullable(monitorBo.getAfterTimeOutAgents()).orElse(0)).build());
        return result;
    }

    /**
     * 各状态持续时长
     *
     * @param intervalStart                  时段开始时间
     * @param intervalEnd                    时段结束时间
     * @param startEndDateTimeDurationBoList 时间信息
     * @return StateDuration
     */
    private int getIntervalStateDuration(LocalDateTime intervalStart, LocalDateTime intervalEnd, List<StartEndDateTimeDurationBo> startEndDateTimeDurationBoList) {
        // 1.开始结束时间在时段内；2.开始时间在时段内但无结束时间；3.开始时间在时段内但结束时间在时段为外；
        // 4.开始结束时间都不在时段内；5.开始时间不在时段且无结束时间；6.开始时间不在时段内但结束时间在时段内；
        long duration = 0;
        for (StartEndDateTimeDurationBo startEndDateTimeDurationBo : startEndDateTimeDurationBoList) {
            LocalDateTime startDateTime = startEndDateTimeDurationBo.getStartDateTime();
            LocalDateTime endDateTime = startEndDateTimeDurationBo.getEndDateTime();
            // 开始时间在结束时段后，提前结束遍历
            if (startDateTime.isAfter(intervalEnd)) {
                break;
            }
            // 开始时间在时段内 或 开始时间等于时段开始时间
            if ((startDateTime.isAfter(intervalStart) || startDateTime.isEqual(intervalStart)) && startDateTime.isBefore(intervalEnd)) {
                // 无结束时间 或 结束时间在时段外 或 结束时间等于时段结束时间
                if (endDateTime == null || endDateTime.isAfter(intervalEnd) || endDateTime.isEqual(intervalEnd)) {
                    duration += Duration.between(startDateTime, intervalEnd).getSeconds();
                    continue;
                } else if (endDateTime.isAfter(intervalStart) && (endDateTime.isBefore(intervalEnd) || endDateTime.isEqual(intervalEnd))) {
                    // 结束时间在时段内 或 结束时间等于时段结束时间
                    duration += startEndDateTimeDurationBo.getDuration();
                    continue;
                }
            }
            // 开始时间不在时段内
            if (startDateTime.isBefore(intervalStart)) {
                // 无结束时间 或 结束时间在时段外 或 结束时间等于时段结束时间
                if (endDateTime == null || endDateTime.isAfter(intervalEnd) || endDateTime.isEqual(intervalEnd)) {
                    duration += Duration.between(intervalStart, intervalEnd).getSeconds();
                } else if (endDateTime.isAfter(intervalStart) && (endDateTime.isBefore(intervalEnd) || endDateTime.isEqual(intervalEnd))) {
                    // 结束时间在时段内
                    duration += Duration.between(intervalStart, endDateTime).getSeconds();
                }
            }
        }
        return (int) duration;
    }

    /**
     * 获取各时段一线人力
     *
     * @param dto            查询条件
     * @param localDateTimes 工作时间段
     * @return 各时段一线人力
     */
    private Map<LocalDateTime, Double> getFrontLineManpowerMap(DateDurationQueueIdDto dto, List<LocalDateTime> localDateTimes) {
        if (dto.getQueueId().isEmpty()) {
            return null;
        }
        Map<LocalDateTime, Double> frontLineManpowerMap = new HashMap<>(localDateTimes.size());
        // 员工-人力系数映射关系
        List<HumanResCoef> humanResCoefList = ccBaseConfigDao.selectHumanResCoefByPage();
        // 查库-坐席各状态持续时长
        List<DndStatsBo> dndStatsBoList = dosCallCenterDndStatsDao.selectCheckInSeat(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());
        for (LocalDateTime intervalStart : localDateTimes) {
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            // 计算各坐席在该时段内持续时间
            Map<String, Long> agentWorkDurationMap = getAgentIntervalDuration(intervalStart, intervalEnd, dndStatsBoList);

            double frontLineManpower = 0;
            // 获取除就餐和培训的其他时间超过0分钟的员工id
            for (Map.Entry<String, Long> entry : agentWorkDurationMap.entrySet()) {
                if (entry.getValue() > CcBoardConstant.CHECK_IN_PASS_LINE_SECOND) {
                    String agentId = entry.getKey();
                    for (HumanResCoef humanResCoef : humanResCoefList) {
                        if (humanResCoef.getJobNo().equals(agentId)) {
                            frontLineManpower += humanResCoef.getManPowerCoef();
                            break;
                        }
                    }
                }
            }
            frontLineManpowerMap.put(intervalStart, BigDecimal.valueOf(frontLineManpower).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        return frontLineManpowerMap;
    }

    /**
     * 查时段在线及工时表格（除全天工作时间工作标记）
     *
     * @param dto 查询条件
     * @return TimeSlotOnlineWorkingHourTableVo
     */
    private List<TimeSlotOnlineWorkingHourTableVo> getTimeSlotOnlineWorkingHourTable(DateDurationQueueIdPageDto dto) {
        List<String> statusList = getCcWorkStatus(CcBoardConstant.CC_WORK_STATUS);
        List<String> serviceStatusList = getCcWorkStatus(CcBoardConstant.CC_SERVICE_STATUS);
        return dosCallCenterRecordDao.selectTimeSlotOnlineWorkingHourTable(dto, statusList, serviceStatusList);
    }

    /**
     * 时段在线及工时页-全天工作时间工作标记
     *
     * @param dto          查询条件
     * @param agentIdList  坐席id
     * @param dateTimeType 1 HH:mm; 2 yyyy-MM-dd HH:mm:ss
     * @return 坐席各时段在线标记
     */
    private Map<String, List<TimeDurationVo>> getAgentTimeDurationFlag(DateDurationQueueIdPageDto dto, List<String> agentIdList, int dateTimeType) {
        List<LocalDateTime> intervalList = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()), ChronoUnit.MINUTES, 30);
        // 查库-坐席各状态持续时长
        List<DndStatsBo> dndStatsBoList = dosCallCenterDndStatsDao.selectCheckInSeat(dto.getStartDate(), dto.getEndDate(), dto.getQueueId());

        Map<String, List<TimeDurationVo>> result = new HashMap<>(agentIdList.size());
        agentIdList.forEach(agentId -> result.put(agentId, new ArrayList<>(intervalList.size())));

        for (LocalDateTime intervalStart : intervalList) {
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            // 计算各坐席在该时段内持续时间
            Map<String, Long> agentWorkDurationMap = getAgentIntervalDuration(intervalStart, intervalEnd, dndStatsBoList);

            // 以入参坐席id为主
            for (String agentId : agentIdList) {
                Long duration = agentWorkDurationMap.getOrDefault(agentId, null);
                String interval = null;
                if (dateTimeType == 1) {
                    interval = intervalStart.toLocalTime().toString();
                } else if (dateTimeType == 2) {
                    interval = intervalStart.format(DatePattern.NORM_DATETIME_FORMATTER);
                }
                TimeDurationVo build = TimeDurationVo.builder().duration(interval).build();
                if (duration != null && duration > CcBoardConstant.CHECK_IN_PASS_LINE_SECOND) {
                    build.setFlag(1);
                } else {
                    build.setFlag(0);
                }
                List<TimeDurationVo> timeDurationVoList = result.get(agentId);
                timeDurationVoList.add(build);
                result.put(agentId, timeDurationVoList);
            }
        }
        return result;
    }

    /**
     * 获取坐席时段内在线持续时间
     *
     * @param intervalStart  时段开始时间
     * @param intervalEnd    时段结束时间
     * @param dndStatsBoList 状态信息
     * @return 各坐席时段内在线持续时间
     */
    private Map<String, Long> getAgentIntervalDuration(LocalDateTime intervalStart, LocalDateTime intervalEnd, List<DndStatsBo> dndStatsBoList) {
        // 1.开始结束时间在时段内；2.开始时间在时段内但无结束时间；3.开始时间在时段内但结束时间在时段为外；
        // 4.开始结束时间都不在时段内；5.开始时间不在时段且无结束时间；6.开始时间不在时段内但结束时间在时段内；
        Map<String, Long> agentWorkDurationMap = new HashMap<>();
        for (DndStatsBo dndStatsBo : dndStatsBoList) {
            LocalDateTime startDateTime = dndStatsBo.getStartDateTime();
            LocalDateTime endDateTime = dndStatsBo.getEndDateTime();
            // 开始时间在结束时段后，提前结束遍历
            if (startDateTime.isAfter(intervalEnd)) {
                continue;
            }
            String agentId = dndStatsBo.getAgentId();
            long duration = 0;
            // 开始时间在时段内
            if (startDateTime.isAfter(intervalStart) && startDateTime.isBefore(intervalEnd)) {
                // 无结束时间或结束时间在时段外
                if (endDateTime == null || endDateTime.isAfter(intervalEnd)) {
                    duration += Duration.between(startDateTime, intervalEnd).getSeconds();
                } else if (endDateTime.isAfter(intervalStart) && endDateTime.isBefore(intervalEnd)) {
                    // 结束时间在时段内
                    duration += dndStatsBo.getDuration();
                }
            }
            // 开始时间不在时段内
            if (startDateTime.isBefore(intervalStart)) {
                // 无结束时间或结束时间在时段外
                if (endDateTime == null || endDateTime.isAfter(intervalEnd)) {
                    duration += Duration.between(intervalStart, intervalEnd).getSeconds();
                } else if (endDateTime.isAfter(intervalStart) && endDateTime.isBefore(intervalEnd)) {
                    // 结束时间在时段内
                    duration += Duration.between(intervalStart, endDateTime).getSeconds();
                }
            }
            agentWorkDurationMap.put(agentId, agentWorkDurationMap.getOrDefault(agentId, 0L) + duration);
        }
        return agentWorkDurationMap;
    }

    /**
     * 获取时段内人力数据图表
     *
     * @return HumanDataChartVo
     */
    private List<HumanDataChartBo> getHumanDataChartBo(DateDurationQueueIdDto dto) {
        return dosCallCenterRecordDao.selectHumanDataChartBo(dto);
    }

    /**
     * 组装10s率
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleTenSecondRate(AllDayTenSecondRateContextHolder contextHolder) {
        Integer tenSecondConnectionNumber = contextHolder.getTenSecondConnectionNumber();
        Integer accessNumber = contextHolder.getAccessNumber();
        if (accessNumber == 0) {
            contextHolder.getContext().setResult("0");
            return;
        }
        BigDecimal tenSecondRate = BigDecimal.valueOf(tenSecondConnectionNumber != null ? tenSecondConnectionNumber : 0).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(accessNumber), 2, RoundingMode.HALF_UP);
        contextHolder.getContext().setResult(tenSecondRate.toString());
    }

    /**
     * 组装接入量
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleAccessNumber(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer accessNumber = contextHolder.getAccessNumber();
        contextHolder.getContext().getResult().setAccessNumber(accessNumber);
    }

    /**
     * 组装接通率
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleConnectionRate(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer connectionNumber = contextHolder.getConnectionNumber();
        Integer accessNumber = contextHolder.getAccessNumber();
        if (accessNumber == 0) {
            contextHolder.getContext().getResult().setConnectionRate("0");
            return;
        }
        BigDecimal connectionRate = BigDecimal.valueOf(connectionNumber != null ? connectionNumber : 0).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(accessNumber), 2, RoundingMode.HALF_UP);
        contextHolder.getContext().getResult().setConnectionRate(connectionRate.toString());
    }

    /**
     * 组装10s率
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleTenSecondRate(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer tenSecondConnectionNumber = contextHolder.getTenSecondConnectionNumber();
        Integer accessNumber = contextHolder.getAccessNumber();
        if (accessNumber == 0) {
            contextHolder.getContext().getResult().setTenSecondRate("0");
            return;
        }
        BigDecimal tenSecondRate = BigDecimal.valueOf(tenSecondConnectionNumber != null ? tenSecondConnectionNumber : 0).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(accessNumber), 2, RoundingMode.HALF_UP);
        contextHolder.getContext().getResult().setTenSecondRate(tenSecondRate.toString());
    }

    /**
     * 组装10s率目标
     *
     * @param context 上下文
     */
    private void assembleTenSecondRateTarget(ProjectActualTimeMonitorContext context) {
        String yearMonth = LocalDate.parse(context.getRequest().getEndDate()).format(DatePattern.NORM_MONTH_FORMATTER);
        Double tenSecondRateTargetDouble = ccBaseConfigDao.selectConnRateByTargetMonth(yearMonth);
        String tenSecondRateTarget = null;
        if (Objects.nonNull(tenSecondRateTargetDouble)) {
            tenSecondRateTarget = tenSecondRateTargetDouble.toString();
        }
        context.getResult().setTenSecondRateTarget(tenSecondRateTarget);
    }

    /**
     * 组装平均排队时间
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleAvgQueueDuration(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer acdQueueTime = contextHolder.getAcdQueueTime();
        Integer accessNumber = contextHolder.getAccessNumber();
        if (accessNumber == 0) {
            contextHolder.getContext().getResult().setAvgQueueDuration(0);
            return;
        }
        BigDecimal avgQueueDuration = BigDecimal.valueOf(acdQueueTime != null ? acdQueueTime : 0).divide(BigDecimal.valueOf(accessNumber), 0, RoundingMode.HALF_UP);
        contextHolder.getContext().getResult().setAvgQueueDuration(avgQueueDuration.intValue());
    }

    /**
     * 组装平均通话时长
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleAvgCallInDuration(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer talkTime = contextHolder.getTalkTime();
        Integer connectionNumber = contextHolder.getConnectionNumber();
        if (connectionNumber == 0) {
            contextHolder.getContext().getResult().setAvgCallInDuration(0);
            return;
        }
        BigDecimal avgCallInDuration = BigDecimal.valueOf(talkTime != null ? talkTime : 0).divide(BigDecimal.valueOf(connectionNumber), 0, RoundingMode.HALF_UP);
        contextHolder.getContext().getResult().setAvgCallInDuration(avgCallInDuration.intValue());
    }

    /**
     * 组装接入客户数
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleCallInCustomerNumber(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer callInCustomerNumber = contextHolder.getCallInCustomerNumber();
        contextHolder.getContext().getResult().setCallInCustomerNumber(callInCustomerNumber);
    }

    /**
     * 组装接起客户数
     *
     * @param contextHolder 上下文管理器
     */
    private void assembleConnectCustomerNumber(ProjectActualTimeMonitorContextHolder contextHolder) {
        Integer connectCustomerNumber = contextHolder.getConnectCustomerNumber();
        contextHolder.getContext().getResult().setConnectCustomerNumber(connectCustomerNumber);
    }

    /**
     * 跨年则显示为 yyyy-MM-dd；不跨年显示为 MM-dd
     *
     * @param dto    查询条件
     * @param result 数据集
     * @return
     */
    private void assembleDate(DateDurationQueueIdDto dto, List<DateValueChartVo> result) {
        LocalDate startLocalDate = LocalDate.parse(dto.getStartDate());
        LocalDate endLocalDate = LocalDate.parse(dto.getEndDate());
        if (Period.between(startLocalDate, endLocalDate).getYears() == 0) {
            for (DateValueChartVo chartVo : result) {
                chartVo.setDate(LocalDate.parse(chartVo.getDate()).format(DatePatternUtil.NORM_DAY_FORMATTER));
            }
        }
    }
}
