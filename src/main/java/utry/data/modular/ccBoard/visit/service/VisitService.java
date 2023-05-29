package utry.data.modular.ccBoard.visit.service;

import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.visit.dto.*;
import utry.data.modular.ccBoard.visit.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 回访任务Service
 * @author zhongdongbiao
 * @date 2022/10/26 11:23
 */
public interface VisitService {

    /**
     * 获取全部队列
     * @param queueType
     * @return
     */
    List<QueueVo> getQueueList(String queueType);

    /**
     * 获取回访项目监控数据
     * @param dateDurationQueueIdDto
     * @return
     */
    VisitMonitoringVo getVisitMonitoring(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 有效回访率
     * @param dateDurationQueueIdDto
     * @return
     */
    List<EffectiveRateVo> getVisitReclined(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 话务明细
     * @param dateDurationQueueIdDto
     * @return
     */
    List<CallRecordDetail> getCallDetail(DateDurationQueueIdPageDto dateDurationQueueIdDto);

    /**
     * 获取回访记录
     * @param serviceNumber
     * @return
     */
    VisitRecordVo getVisitRecord(String serviceNumber);

    /**
     * 未完成回访项目
     * @param dateDurationQueueIdDto
     * @return
     */
    List<NotCompleteVo> getNoVisitProject(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 回访业务监控
     * @param dateDurationQueueIdDto
     * @return
     */
    VisitBusinessMonitoringVo getVisitBusinessMonitoring(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 已完成回访项目-任务分类维度
     * @param completeProjectDto
     * @return
     */
    List<CompleteProjectVo> getCompleteProject(CompleteProjectDto completeProjectDto);

    /**
     * 已完成回访项目-任务分类维度-灰色
     * @param completeProjectDto
     * @return
     */
    List<GrayProjectVo> getCompleteProjectByGray(CompleteProjectDto completeProjectDto);



    /**
     * 回访结果
     *
     * @param visitResultDto
     * @param accountingCenter
     * @return
     */
    List<VisitResultVo> getVisitResult(VisitResultDto visitResultDto,List<String> accountingCenter);

    /**
     * 申诉统计
     * @param dateDurationQueueIdDto
     * @return
     */
    List<ComplaintVo> getComplaint(CompleteProjectDto dateDurationQueueIdDto);

    /**
     * 回访违约单
     *
     * @param visitDefaultDto
     * @param accountingCenter
     * @return
     */
    List<VisitDefaultVo> getVisitDefault(VisitDefaultDto visitDefaultDto,List<String> accountingCenter);

    /**
     * 回访坐席利用率
     * @param dateDurationQueueIdDto
     * @return
     */
    List<VisitRateVo> getVisitRate(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 违约率
     * @param dateDurationQueueIdDto
     * @return
     */
    List<DefaultRateVo> getDefaultRate(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 回访呼出量/呼通量
     * @param dateDurationQueueIdDto
     * @return
     */
    List<BreatheRateVo> getBreatheRate(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 回访坐席满意度
     * @param dateDurationQueueIdDto
     * @return
     */
    List<SatisfactionRateVo> getSatisfactionRate(DateDurationQueueIdDto dateDurationQueueIdDto);

    /**
     * 呼叫中心-在线坐席-坐席状态表格
     *
     * @param dto
     * @param historyFlag
     * @return
     */
    List<VisitAgentStateTableVo> visitAgentStateTable(VisitTableDto dto, String historyFlag);

    /**
     * 导出图表
     * @param response
     * @param exportConditionDto
     */
    void exportChart(HttpServletResponse response, ExportConditionDto exportConditionDto);
}
