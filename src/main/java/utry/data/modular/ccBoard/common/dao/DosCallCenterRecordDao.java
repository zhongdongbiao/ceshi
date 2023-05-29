package utry.data.modular.ccBoard.common.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.common.vo.AgentCallLogTableVo;
import utry.data.modular.ccBoard.hotLineAgent.bo.*;
import utry.data.modular.ccBoard.hotLineAgent.dto.AgentIdDateDurationPageDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.DateQueueIdDto;
import utry.data.modular.ccBoard.hotLineAgent.dto.StateDateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.hotLineAgent.vo.*;
import utry.data.modular.ccBoard.visit.bo.BreatheRate;
import utry.data.modular.ccBoard.visit.bo.SatisfactionRate;
import utry.data.modular.ccBoard.visit.bo.VisitCompleteNumber;
import utry.data.modular.ccBoard.visit.vo.CallRecordDetail;

import java.util.List;

/**
 * @program: data
 * @description: 通话记录持久层
 * @author: WangXinhao
 * @create: 2022-10-27 15:58
 **/
@Mapper
public interface DosCallCenterRecordDao {

    /**
     * 获取电话呼出量
     *
     * @param dateDurationQueueIdDto 查询条件
     * @param callStatus             接通状态 1 接听 0 未接听
     * @return
     */
    @DS("shuce_db")
    int getCallOutNumber(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("callStatus") String callStatus);

    /**
     * 通话记录数据
     *
     * @param dateDurationQueueIdDto 查询条件
     * @return CallRecordDetail
     */
    @DS("shuce_db")
    List<CallRecordDetail> getCallRecordDetail(@Param("dateDurationQueueIdDto") DateDurationQueueIdPageDto dateDurationQueueIdDto);

    /**
     * 查询接入量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return 接入量
     */
    @DS("shuce_db")
    Integer selectAccessNumber(@Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("queueId") List<String> queueId);

    /**
     * 查询坐席接起量
     *
     * @param request 查询条件
     * @return 坐席接起量
     */
    @DS("shuce_db")
    Integer selectConnectionNumber(DateDurationQueueIdDto request);

    /**
     * 查询10s内坐席接起量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return 10s内坐席接起量
     */
    @DS("shuce_db")
    Integer selectTenSecondConnectionNumber(@Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            @Param("queueId") List<String> queueId);

    /**
     * 查询ACD排队时间
     *
     * @param request 查询条件
     * @return ACD排队时间
     */
    @DS("shuce_db")
    Integer selectAcdQueueTime(DateDurationQueueIdDto request);

    /**
     * 查询通话时间
     *
     * @param request 查询条件
     * @return 通话时间
     */
    @DS("shuce_db")
    Integer selectTalkTime(DateDurationQueueIdDto request);

    /**
     * 查询10s接通率表格
     *
     * @param dto        查询条件
     * @param statusList 工时利用率状态
     * @return TenSecondRateTableVo
     */
    @DS("shuce_db")
    List<TenSecondRateTableVo> selectTenSecondRateTable(@Param("dto") DateDurationQueueIdPageDto dto,
                                                        @Param("statusList") List<String> statusList);

    /**
     * 查询呼入日周月满意度百分比
     *
     * @param dto 查询条件
     * @return SatisfactionVo
     */
    @DS("shuce_db")
    SatisfactionVo selectCallInDailyWeeklyMonthlySatisfaction(DateQueueIdDto dto);

    /**
     * 查询呼出日周月满意度百分比
     *
     * @param dto 查询条件
     * @return SatisfactionVo
     */
    @DS("shuce_db")
    SatisfactionVo selectCallOutDailyWeeklyMonthlySatisfaction(DateQueueIdDto dto);

    /**
     * 查人力数据图表（除了在线坐席量）
     *
     * @param dto 查询条件
     * @return HumanDataChartBo
     */
    @DS("shuce_db")
    List<HumanDataChartBo> selectHumanDataChartBo(DateDurationQueueIdDto dto);

    /**
     * 查时段在线工时表格
     *
     * @param dto               查询条件
     * @param statusList        工时利用率状态
     * @param serviceStatusList 单位小时接入量状态
     * @return TimeSlotOnlineWorkingHourTableVo
     */
    @DS("shuce_db")
    List<TimeSlotOnlineWorkingHourTableVo> selectTimeSlotOnlineWorkingHourTable(@Param("dto") DateDurationQueueIdPageDto dto,
                                                                                @Param("statusList") List<String> statusList,
                                                                                @Param("serviceStatusList") List<String> serviceStatusList);

    /**
     * 查询进线统计图表
     *
     * @param dto 查询条件
     * @return IncomeStatisticChartVo
     */
    @DS("shuce_db")
    List<IncomeStatisticChartBo> selectIncomeStatisticChart(DateDurationQueueIdDto dto);


    /**
     * 获取回访呼出率
     *
     * @param dto
     * @return
     */
    @DS("shuce_db")
    List<BreatheRate> getBreatheRate(DateDurationQueueIdDto dto);

    /**
     * 获取回访呼出量
     *
     * @param dto
     * @return
     */
    @DS("shuce_db")
    List<VisitCompleteNumber> getBreatheNumber(DateDurationQueueIdDto dto);

    /**
     * 获取满意率
     *
     * @param dto
     * @return
     */
    @DS("shuce_db")
    List<SatisfactionRate> getSatisfactionRate(DateDurationQueueIdDto dto);

    /**
     * 查量级数据图表（除一线人力、理论接起量）
     *
     * @param dto 查询条件
     * @return IncomeStatisticChartVo
     */
    @DS("shuce_db")
    List<MagnitudeDataChartBo> selectMagnitudeDataChart(DateDurationQueueIdDto dto);

    /**
     * 查询各时段单位小时接入量/单位小时完成量
     *
     * @param dto      查询条件
     * @param callType in呼入；out呼出
     * @return AgentManHourUtilizationRateChartBo
     */
    @DS("shuce_db")
    List<IntervalCallDurationRingTimeHourlyCallNumberBo> selectIntervalCallDurationRingTimeBo(@Param("dto") DateDurationQueueIdDto dto,
                                                                                              @Param("callType") String callType);

    /**
     * 坐席工时利用率图表-总体CPH/总体工时利用率
     *
     * @param dto          查询条件
     * @param callType     in呼入；out呼出
     * @param statusList   工时利用率状态
     * @param inCphStatus  热线CPH分母状态
     * @param outCphStatus 回访CPH分母状态
     * @return AgentManHourUtilizationRateTotalVo
     */
    @DS("shuce_db")
    AgentManHourUtilizationRateTotalVo selectAgentManHourUtilizationRateTotal(@Param("dto") DateDurationQueueIdDto dto,
                                                                              @Param("callType") String callType,
                                                                              @Param("statusList") List<String> statusList,
                                                                              @Param("inCphStatusList") List<String> inCphStatus,
                                                                              @Param("outCphStatusList") List<String> outCphStatus);

    /**
     * 在线坐席-坐席状态表格
     *
     * @param dto               查询条件
     * @param statusList        工时利用率状态
     * @param serviceStatusList 单位小时接通量状态
     * @param historyFlag       1:查历史；0:查当天
     * @return AgentStateTableVo
     */
    @DS("shuce_db")
    List<AgentStateTableVo> selectAgentStateTable(@Param("dto") StateDateDurationQueueIdPageDto dto,
                                                  @Param("statusList") List<String> statusList,
                                                  @Param("serviceStatusList") List<String> serviceStatusList,
                                                  @Param("historyFlag") Integer historyFlag);

    /**
     * 呼入通话记录表格
     *
     * @param dto 查询条件
     * @return AgentCallLogTableVo
     */
    @DS("shuce_db")
    List<AgentCallLogTableVo> selectCallInAgentCallLogTable(AgentIdDateDurationPageDto dto);

    /**
     * 呼出通话记录表格
     *
     * @param dto 查询条件
     * @return AgentCallLogTableVo
     */
    @DS("shuce_db")
    List<AgentCallLogTableVo> selectCallOutAgentCallLogTable(AgentIdDateDurationPageDto dto);

    /**
     * 查询坐席间隔半小时的通话时长
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return AgentIntervalCallDurationBo
     */
    @DS("shuce_db")
    List<AgentIntervalCallDurationBo> selectAgentIntervalDuration(@Param("startDate") String startDate,
                                                                  @Param("endDate") String endDate,
                                                                  @Param("queueId") List<String> queueId);

    /**
     * 热线服务单轴
     *
     * @param soundRecordFileName 录音文件名
     * @return ServiceOrderDetailAxisBo
     */
    @DS("shuce_db")
    ServiceOrderDetailAxisBo selectServiceOrderDetailAxis(String soundRecordFileName);

    /**
     * 根据日期聚合查询接入量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DateValueChartVo
     */
    @DS("shuce_db")
    List<DateValueChartVo> selectAccessNumberGroupByDate(@Param("startDate") String startDate,
                                                         @Param("endDate") String endDate,
                                                         @Param("queueId") List<String> queueId);

    /**
     * 根据日期聚合查询接通率
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DateValueChartVo
     */
    @DS("shuce_db")
    List<DateValueChartVo> selectConnectionRateGroupByDate(@Param("startDate") String startDate,
                                                           @Param("endDate") String endDate,
                                                           @Param("queueId") List<String> queueId);

    /**
     * 根据日期聚合查询10s接通率
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DateValueChartVo
     */
    @DS("shuce_db")
    List<DateValueChartVo> selecttenSecondRateGroupByDate(@Param("startDate") String startDate,
                                                          @Param("endDate") String endDate,
                                                          @Param("queueId") List<String> queueId);

    /**
     * 根据日期聚合查询平均排队时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DateValueChartVo
     */
    @DS("shuce_db")
    List<DateValueChartVo> selectAvgQueueDurationGroupByDate(@Param("startDate") String startDate,
                                                             @Param("endDate") String endDate,
                                                             @Param("queueId") List<String> queueId);

    /**
     * 根据日期聚合查询平均通话时长
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param queueId   队列id
     * @return DateValueChartVo
     */
    @DS("shuce_db")
    List<DateValueChartVo> selectAvgCallInDurationGroupByDate(@Param("startDate") String startDate,
                                                              @Param("endDate") String endDate,
                                                              @Param("queueId") List<String> queueId);

    /**
     * 查询接入客户数
     *
     * @param request 查询条件
     * @return 接入客户数
     */
    @DS("shuce_db")
    Integer selectCallInCustomerNumber(DateDurationQueueIdDto request);

    /**
     * 查询接起客户数
     *
     * @param request 查询条件
     * @return 接起客户数
     */
    @DS("shuce_db")
    Integer selectConnectCustomerNumber(DateDurationQueueIdDto request);

    /**
     * 查人力数据表格
     *
     * @param dto 查询条件
     * @return HumanDataTableVo
     */
//    @DS("shuce_db")
    List<HumanDataTableBo> selectHumanDataTable(DateDurationQueueIdDto dto);
}
