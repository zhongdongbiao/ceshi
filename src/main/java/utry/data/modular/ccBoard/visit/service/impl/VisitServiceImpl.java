package utry.data.modular.ccBoard.visit.service.impl;

import cn.hutool.core.date.DatePattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.data.modular.baseConfig.dao.CcBaseConfigDao;
import utry.data.modular.baseConfig.dao.CcBaseDataDao;
import utry.data.modular.baseConfig.model.CCSeatInfo;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.modular.ccBoard.common.dao.DosCallCenterCheckStatsDao;
import utry.data.modular.ccBoard.common.dao.DosCallCenterRecordDao;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdPageDto;
import utry.data.modular.ccBoard.common.service.CommonService;
import utry.data.modular.ccBoard.common.vo.ChartPointTwoValueVo;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;
import utry.data.modular.ccBoard.hotLineAgent.bo.StartEndDateTimeDurationBo;
import utry.data.modular.ccBoard.hotLineAgent.dto.HotLineExportConditionDto;
import utry.data.modular.ccBoard.hotLineAgent.service.HotOrderFollowProcessService;
import utry.data.modular.ccBoard.visit.bo.*;
import utry.data.modular.ccBoard.visit.dao.VisitAuditDao;
import utry.data.modular.ccBoard.visit.dao.VisitDao;
import utry.data.modular.ccBoard.visit.dao.VisitDefaultDao;
import utry.data.modular.ccBoard.visit.dto.*;
import utry.data.modular.ccBoard.visit.service.VisitService;
import utry.data.modular.ccBoard.visit.vo.*;
import utry.data.modular.technicalQuality.controller.TechnicalQualityController;
import utry.data.modular.technicalQuality.utils.Column;
import utry.data.modular.technicalQuality.utils.ExcelTool;
import utry.data.modular.technicalQuality.utils.TitleEntity;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 回访任务实现类
 * @author zhongdongbiao
 * @date 2022/10/26 11:24
 */
@Service
public class VisitServiceImpl implements VisitService {

    @Resource
    private VisitDao visitDao;

    @Resource
    private VisitAuditDao visitAuditDao;

    @Resource
    private VisitDefaultDao visitDefaultDao;

    @Resource
    private DosCallCenterRecordDao dosCallCenterRecordDao;

    @Resource
    private CcBaseConfigDao ccBaseConfigDao;

    @Autowired
    private CcBaseDataDao ccBaseDataDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HotOrderFollowProcessService hotOrderFollowProcessService;

    @Resource
    private DosCallCenterCheckStatsDao dosCallCenterCheckStatsDao;

    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TechnicalQualityController.class);

    @Override
    public List<QueueVo> getQueueList(String queueType) {
        List<QueueVo> list = new ArrayList<>();
        QueueVo queueVo = new QueueVo();
        if("1".equals(queueType)){
            queueVo.setQueueId("1");
            queueVo.setQueueName("回访坐席");
        }else {
            queueVo.setQueueId("2");
            queueVo.setQueueName("热线坐席");
        }
        queueVo.setLabel("0");
        // 获取部门下面的队列
        List<QueueVo> queueList = visitAuditDao.getQueueList(queueType);
        queueVo.setChildren(queueList);
        list.add(queueVo);
        return list;
    }

    @Override
    public VisitMonitoringVo getVisitMonitoring(DateDurationQueueIdDto dateDurationQueueIdDto) {
        VisitMonitoringVo visitMonitoring = new VisitMonitoringVo();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 当月完成量
        int monthCompleteCount = visitAuditDao.monthCompleteCount(dateDurationQueueIdDto,accountingCenter,"1");
        // 当月回访记录审核全部
        int monthVisitCount = visitAuditDao.monthCompleteCount(dateDurationQueueIdDto,accountingCenter,null);
        // 当月违约量
        int monthDefaultCount = visitDefaultDao.monthDefaultCount(dateDurationQueueIdDto,accountingCenter,"1");
        // 总工作时长
        int workTime = dosCallCenterCheckStatsDao.getWorkTime(dateDurationQueueIdDto);
        // 电话呼出量
        int callOutNumber = dosCallCenterRecordDao.getCallOutNumber(dateDurationQueueIdDto,null);
        // 电话呼通量
        int isCallOutNumber = dosCallCenterRecordDao.getCallOutNumber(dateDurationQueueIdDto,"1");
        visitMonitoring.setBreatheNumber(callOutNumber);
        // 呼通量/呼出量
        visitMonitoring.setBreatheRate(TimeUtil.getRate(isCallOutNumber,callOutNumber));
        // 当日回访总量/总工作时长
        visitMonitoring.setCompleteNumber(TimeUtil.getDouble(callOutNumber,workTime/3600));
        // 当日回访完成量
        visitMonitoring.setVisitNumber(callOutNumber);
        // 当月有效回访率
        // 服务回访记录审核表回访结果描述选完成单量+回访违约表审核结果为违约单量（修改了）/（当月回访记录审核全部+回访违约单全部）
        visitMonitoring.setVisitRate(TimeUtil.getRate(monthCompleteCount+monthDefaultCount,monthVisitCount));
        return visitMonitoring;
    }

    @Override
    public List<EffectiveRateVo> getVisitReclined(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 回显列表
        List<EffectiveRateVo> effectiveRateVos = new ArrayList<>();
        //把String转为LocalDate
        LocalDate localTime=LocalDate.parse(dateDurationQueueIdDto.getEndDate(),DatePattern.NORM_DATE_FORMATTER);
        //判断当前日期是否大于指定日期
        if(!LocalDate.now().isAfter(localTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateDurationQueueIdDto.setEndDate(sdf.format(new Date()));
        }
        List<String> daysStr = TimeUtil.findDaysStr(dateDurationQueueIdDto.getStartDate(), dateDurationQueueIdDto.getEndDate(), "0");

        for (String date : daysStr) {
            // 单日回显Vo
            EffectiveRateVo effectiveRateVo = new EffectiveRateVo();
            effectiveRateVo.setDate(date);

            // 单日图例列表
            List<TwoLegend> queueEffectives = new ArrayList<>();

            // 有效回访量
            TwoLegend effectiveNumber = new TwoLegend();
            effectiveNumber.setLabel("有效回访量");
            int count=0;
            for (int i = 0; i < dateDurationQueueIdDto.getQueueId().size(); i++) {

                List<String> queueId = new ArrayList<>();
                queueId.add(dateDurationQueueIdDto.getQueueId().get(i));

                // 根据队列id获取核算中心
                List<String> accountingCenter = visitAuditDao.getQueueDeptList(queueId);

                // 有效回访率
                TwoLegend queueEffective = new TwoLegend();
                queueEffective.setLabel(dateDurationQueueIdDto.getQueueId().get(i)+"");
                // 获取队列每日单日完成量
                int everyDayCount = visitAuditDao.getEveryDayCount(date,accountingCenter,"1");
                // 获取队列每日回访记录审核全部
                int everyDayVisitCount = visitAuditDao.getEveryDayCount(date,accountingCenter,null);
                // 获取队列每日违约量
                int everyDayDefaultCount = visitDefaultDao.getEveryDayDefaultCount(date,accountingCenter,"1");
                if(everyDayVisitCount!=0){
                    count = count + everyDayCount + everyDayDefaultCount;
                    queueEffective.setNumber(TimeUtil.getRate(everyDayCount+everyDayDefaultCount,everyDayVisitCount));
                    queueEffectives.add(queueEffective);
                }else {
                    queueEffective.setNumber(0.0);
                    queueEffectives.add(queueEffective);
                }
            }
            effectiveNumber.setNumber(Double.parseDouble(count+""));
            queueEffectives.add(effectiveNumber);

            effectiveRateVo.setQueueEffectives(queueEffectives);

            effectiveRateVos.add(effectiveRateVo);
        }
        
        return effectiveRateVos;
    }

    /**
     * 话务明细
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<CallRecordDetail> getCallDetail(DateDurationQueueIdPageDto dateDurationQueueIdDto) {
        if(dateDurationQueueIdDto.getStartDate().length()<16){
            dateDurationQueueIdDto.setStartDate(dateDurationQueueIdDto.getStartDate()+" 00:00:00");
        }
        if(dateDurationQueueIdDto.getStartDate().length()==16){
            dateDurationQueueIdDto.setStartDate(dateDurationQueueIdDto.getStartDate()+":00");
        }
        if(dateDurationQueueIdDto.getEndDate().length()<16){
            dateDurationQueueIdDto.setEndDate(dateDurationQueueIdDto.getEndDate()+" 23:59:59");
        }
        if(dateDurationQueueIdDto.getEndDate().length()==16){
            dateDurationQueueIdDto.setEndDate(dateDurationQueueIdDto.getEndDate()+":59");
        }

        // 获取通话记录数据
        List<CallRecordDetail> callRecordDetails = dosCallCenterRecordDao.getCallRecordDetail(dateDurationQueueIdDto);

        // 文件名称
        List<String> accountCode = new ArrayList<>();
        callRecordDetails.forEach( callRecordDetail -> {
            accountCode.add(callRecordDetail.getAccountCode());
        });
        // 话务明细
        List<CallDetailVo> callDetailVos = visitAuditDao.getCallDetail(accountCode);
        callRecordDetails.forEach(callRecordDetail -> {
            callDetailVos.forEach(callDetailVo -> {
                // 根据回访单号将通话记录与热线服务单进行关联
                if(callRecordDetail.getAccountCode()!=null
                        && !"".equals(callRecordDetail.getAccountCode()) && callRecordDetail.getAccountCode().equals(callDetailVo.getServiceNumber())){
                    callRecordDetail.setDept(callDetailVo.getDept());
                    callRecordDetail.setHotlineNumber(callDetailVo.getHotlineNumber());
                    callRecordDetail.setVisitTime(callDetailVo.getVisitTime());
                    callRecordDetail.setResult(callDetailVo.getResult());
                    callRecordDetail.setDept(callDetailVo.getDept());
                    callRecordDetail.setProductCategory(callDetailVo.getProductCategory());
                    callRecordDetail.setProductType(callDetailVo.getProductType());
                    callRecordDetail.setServiceType(callDetailVo.getServiceType());
                    callRecordDetail.setServiceDetail(callDetailVo.getServiceDetail());
                    callRecordDetail.setServiceNumber(callDetailVo.getServiceNumber());
                    callRecordDetail.setDispatchingOrder(callDetailVo.getDispatchingOrder());
                }
            });
        });
        return callRecordDetails;
    }

    /**
     * 获取回访记录
     * @param serviceNumber
     * @return
     */
    @Override
    public VisitRecordVo getVisitRecord(String serviceNumber) {
        VisitRecordVo visitRecordVo = new VisitRecordVo();
        VisitAuditBo visitAuditBo = visitAuditDao.getVisitRecord(serviceNumber);
        if(visitAuditBo.getVisitNote()!=null){
            visitAuditBo.setVisitNote(visitAuditBo.getVisitNote().toString().replace("\r\n", "\n").replace("\\r\\n","\n").replace("\r", "\n"));
        }
        visitRecordVo.setVisitAudit(visitAuditDao.getVisitRecord(serviceNumber));
        visitRecordVo.setVisitDefault(visitDefaultDao.getVisitDefaultDetail(serviceNumber));
        return visitRecordVo;
    }

    /**
     * 未完成回访项目
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<NotCompleteVo> getNoVisitProject(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 未完成回访项目Vo
        List<NotCompleteVo> notCompleteVos = new ArrayList<>();

        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());

        // 获取未完成回访项目数据
        List<NotComplete> notCompletes = visitDao.getNotCompete(dateDurationQueueIdDto,accountingCenter);

        notCompletes.forEach(notComplete -> {
           // 给vo设置图例
           NotCompleteVo notCompleteVo = new NotCompleteVo();
           notCompleteVo.setType(notComplete.getType());
           notCompleteVo.setNotCompleteTotal(notComplete.getNotCompleteTotal());

           // 设置待完成图例
            ChartPointVo pending= new ChartPointVo();
            pending.setLabel("待完成");
            pending.setNumber(notComplete.getPending());
            notCompleteVo.setPending(pending);

            // 设置超三天未完成图例
            ChartPointVo threeNotComplete= new ChartPointVo();
            threeNotComplete.setLabel("超三天未完成");
            threeNotComplete.setNumber(notComplete.getThreeNotComplete());
            notCompleteVo.setThreeNotComplete(threeNotComplete);

            // 设置待完成率图例
            ChartPointVo rate= new ChartPointVo();
            rate.setLabel("未完成率");
            rate.setNumber(notComplete.getRate());
            notCompleteVo.setRate(rate);

            notCompleteVos.add(notCompleteVo);

        });
        return notCompleteVos;
    }

    /**
     * 回访业务监控
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public VisitBusinessMonitoringVo getVisitBusinessMonitoring(DateDurationQueueIdDto dateDurationQueueIdDto) {
        VisitBusinessMonitoringVo visitBusinessMonitoringVo = new VisitBusinessMonitoringVo();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 回访任务数量
        Integer visitTask = visitDao.getVisitTask(dateDurationQueueIdDto,null,accountingCenter);
        // 未完成工单数量
        Integer notCompleteWork =visitDao.getVisitTask(dateDurationQueueIdDto,"0",accountingCenter);
        // 已完成回访未审核
        Integer complete;
        // 未申诉
        Integer noComplaint;
         // 第二次审核
        Integer audit;
        //回访结案
        Integer visitCase;
        complete = visitAuditDao.getComplete(dateDurationQueueIdDto,accountingCenter,"0");
        visitCase = visitAuditDao.getComplete(dateDurationQueueIdDto,accountingCenter,"1");
        noComplaint = visitDefaultDao.getNoComplaint(dateDurationQueueIdDto,accountingCenter,"0");
        audit = visitDefaultDao.getNoComplaint(dateDurationQueueIdDto,accountingCenter,"1");

        visitBusinessMonitoringVo.setVisitTask(visitTask);
        visitBusinessMonitoringVo.setNotCompleteWork(notCompleteWork);
        visitBusinessMonitoringVo.setComplete(complete);
        visitBusinessMonitoringVo.setNoComplaint(notCompleteWork);
        visitBusinessMonitoringVo.setNoComplaint(noComplaint);
        visitBusinessMonitoringVo.setAudit(audit);
        visitBusinessMonitoringVo.setVisitCase(visitCase);
        return visitBusinessMonitoringVo;
    }

    /**
     * 已完成回访项目-任务分类维度
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<CompleteProjectVo> getCompleteProject(CompleteProjectDto dateDurationQueueIdDto) {
        List<CompleteProjectVo> completeProjectVos = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 获取已完成回访项目-任务分类维度
        List<CompleteProject> completeByTaskType = new ArrayList<>();
        // 判断是什么维度统计数据
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                completeByTaskType = visitAuditDao.getCompleteByTaskType(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                completeByTaskType = visitAuditDao.getCompleteByRegion(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                completeByTaskType = visitAuditDao.getCompleteByProductCategory(dateDurationQueueIdDto, accountingCenter);
                break;
            case "3":
                completeByTaskType = visitAuditDao.getCompleteByTable(dateDurationQueueIdDto, accountingCenter);
        }

        completeByTaskType.forEach(completeProject -> {
            CompleteProjectVo completeProjectVo = new CompleteProjectVo();
            completeProjectVo.setAbscissa(completeProject.getAbscissa());
            completeProjectVo.setTotal(completeProject.getTotal());

             // 完成数量
            ChartPointTwoValueVo complete = new ChartPointTwoValueVo();
            complete.setLabel("完成");
            complete.setNumber(completeProject.getComplete());
            complete.setNumber2(completeProject.getCompleteRate());
            completeProjectVo.setComplete(complete);

            // 灰色数量
            ChartPointTwoValueVo gray = new ChartPointTwoValueVo();
            gray.setLabel("灰色");
            gray.setNumber(completeProject.getGray());
            gray.setNumber2(completeProject.getGrayRate());
            completeProjectVo.setGray(gray);

            // 违约
            ChartPointTwoValueVo defaultCount = new ChartPointTwoValueVo();
            defaultCount.setLabel("违约");
            defaultCount.setNumber(completeProject.getDefaultCount());
            defaultCount.setNumber2(completeProject.getDefaultCountRate());
            completeProjectVo.setDefaultCount(defaultCount);

            completeProjectVos.add(completeProjectVo);
        });
        return completeProjectVos;
    }

    /**
     * 已完成回访项目-任务分类维度-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<CompleteProject> exportGetCompleteProject(CompleteProjectDto dateDurationQueueIdDto) {
        // 获取已完成回访项目-任务分类维度
        List<CompleteProject> completeByTaskType = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 判断是什么维度统计数据
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                completeByTaskType = visitAuditDao.getCompleteByTaskType(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                completeByTaskType = visitAuditDao.getCompleteByRegion(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                completeByTaskType = visitAuditDao.getCompleteByProductCategory(dateDurationQueueIdDto, accountingCenter);
                break;
            case "3":
                completeByTaskType = visitAuditDao.getCompleteByTable(dateDurationQueueIdDto, accountingCenter);
        }

        return completeByTaskType;
    }

    /**
     * 已完成回访项目-任务分类维度-灰色
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<GrayProjectVo> getCompleteProjectByGray(CompleteProjectDto dateDurationQueueIdDto) {
        List<GrayProjectVo> grayProjectVos = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 已完成回访项目-任务分类维度-灰色
        List<GrayProject> grayProjectList = new ArrayList<>();
        // 判断是什么维度统计数据
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                grayProjectList = visitAuditDao.getCompleteByTaskTypeByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                grayProjectList = visitAuditDao.getCompleteByRegionByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                grayProjectList = visitAuditDao.getCompleteByProductCategoryByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "3":
                grayProjectList = visitAuditDao.getCompleteByTableByGray(dateDurationQueueIdDto, accountingCenter);
        }
        grayProjectList.forEach(grayProject -> {
            GrayProjectVo grayProjectVo = new GrayProjectVo();
            grayProjectVo.setAbscissa(grayProject.getAbscissa());
            grayProjectVo.setTotal(grayProject.getTotal());

            // 不知情
            ChartPointTwoValueVo notKnow = new ChartPointTwoValueVo();
            notKnow.setLabel("不知情");
            notKnow.setNumber(grayProject.getNotKnow());
            notKnow.setNumber2(grayProject.getNotKnowRate());
            grayProjectVo.setNotKnow(notKnow);

            // 未完成
            ChartPointTwoValueVo noComplete = new ChartPointTwoValueVo();
            noComplete.setLabel("未完成");
            noComplete.setNumber(grayProject.getNoComplete());
            noComplete.setNumber2(grayProject.getNoCompleteRate());
            grayProjectVo.setNoComplete(noComplete);

            // 无人接听
            ChartPointTwoValueVo noAnswering = new ChartPointTwoValueVo();
            noAnswering.setLabel("无人接听");
            noAnswering.setNumber(grayProject.getNoAnswering());
            noAnswering.setNumber2(grayProject.getNoAnsweringRate());
            grayProjectVo.setNoAnswering(noAnswering);

            // 停机
            ChartPointTwoValueVo downtime = new ChartPointTwoValueVo();
            downtime.setLabel("停机");
            downtime.setNumber(grayProject.getDowntime());
            downtime.setNumber2(grayProject.getDowntimeRate());
            grayProjectVo.setDowntime(downtime);

            // 拒接
            ChartPointTwoValueVo reject = new ChartPointTwoValueVo();
            reject.setLabel("拒接");
            reject.setNumber(grayProject.getReject());
            reject.setNumber2(grayProject.getRejectRate());
            grayProjectVo.setReject(reject);

            // 传真
            ChartPointTwoValueVo fax = new ChartPointTwoValueVo();
            fax.setLabel("传真");
            fax.setNumber(grayProject.getFax());
            fax.setNumber2(grayProject.getFaxRate());
            grayProjectVo.setFax(fax);

            // 拒访
            ChartPointTwoValueVo refusedVisit = new ChartPointTwoValueVo();
            refusedVisit.setLabel("拒访");
            refusedVisit.setNumber(grayProject.getRefusedVisit());
            refusedVisit.setNumber2(grayProject.getRefusedVisitRate());
            grayProjectVo.setRefusedVisit(refusedVisit);

            // 改号
            ChartPointTwoValueVo gaiHao = new ChartPointTwoValueVo();
            gaiHao.setLabel("改号");
            gaiHao.setNumber(grayProject.getGaiHao());
            gaiHao.setNumber2(grayProject.getGaiHaoRate());
            grayProjectVo.setGaiHao(gaiHao);

            // 关机
            ChartPointTwoValueVo turnOff = new ChartPointTwoValueVo();
            turnOff.setLabel("关机");
            turnOff.setNumber(grayProject.getTurnOff());
            turnOff.setNumber2(grayProject.getTurnOffRate());
            grayProjectVo.setTurnOff(turnOff);

            grayProjectVos.add(grayProjectVo);
        });
        return grayProjectVos;
    }

    /**
     * 已完成回访项目-任务分类维度-灰色-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<GrayProject> exportGetCompleteProjectByGray(CompleteProjectDto dateDurationQueueIdDto) {
        // 已完成回访项目-任务分类维度-灰色
        List<GrayProject> grayProjectList = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 判断是什么维度统计数据
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                grayProjectList = visitAuditDao.getCompleteByTaskTypeByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                grayProjectList = visitAuditDao.getCompleteByRegionByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                grayProjectList = visitAuditDao.getCompleteByProductCategoryByGray(dateDurationQueueIdDto, accountingCenter);
                break;
            case "3":
                grayProjectList = visitAuditDao.getCompleteByTableByGray(dateDurationQueueIdDto, accountingCenter);
        }
        return grayProjectList;
    }

    /**
     * 回访结果
     *
     * @param visitResultDto
     * @param accountingCenter
     * @return
     */
    @Override
    public List<VisitResultVo> getVisitResult(VisitResultDto visitResultDto, List<String> accountingCenter) {
        List<VisitResultVo> visitResultVos = new ArrayList<>();
        visitResultVos =visitAuditDao.getVisitResult(visitResultDto,accountingCenter);
        List<CCSeatInfo> allState = ccBaseConfigDao.getAllState();
        visitResultVos.forEach(visitResultVo -> {
            allState.forEach(state ->{
                if(visitResultVo.getVisitTable()!=null &&
                        !"".equals(visitResultVo.getVisitTable()) &&
                visitResultVo.getVisitTable().equals(state.getRealName())){
                    visitResultVo.setAgentNumber(state.getExtension());
                }
            });
        });
        return visitResultVos;
    }

    /**
     * 申诉统计
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<ComplaintVo> getComplaint(CompleteProjectDto dateDurationQueueIdDto) {
        List<ComplaintVo> completeProjectVos = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 获取申诉统计-任务分类维度
        List<Complaint> complaintList = new ArrayList<>();
        // 判断是什么维度统计数据
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                complaintList = visitDefaultDao.getComplaintByTaskType(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                complaintList = visitDefaultDao.getComplaintByRegion(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                complaintList = visitDefaultDao.getComplaintByProductCategory(dateDurationQueueIdDto, accountingCenter);
                break;

        }
        complaintList.forEach(completeProject -> {
            ComplaintVo complaintVo = new ComplaintVo();
            complaintVo.setAbscissa(completeProject.getAbscissa());

            // 申诉率
            ChartPointVo complaintRate = new ChartPointVo();
            complaintRate.setLabel("申诉率");
            if(completeProject.getComplaintRate()!=null){
                complaintRate.setNumber(completeProject.getComplaintRate());
            }else {
                complaintRate.setNumber("0.00");
            }
            complaintVo.setComplaintRate(complaintRate);

            // 申诉不通过率
            ChartPointVo noComplaintRate = new ChartPointVo();
            noComplaintRate.setLabel("申诉不通过率");
            if(completeProject.getNoComplaintRate()!=null){
                noComplaintRate.setNumber(completeProject.getNoComplaintRate());
            }else {
                noComplaintRate.setNumber("0.00");
            }
            complaintVo.setNoComplaintRate(noComplaintRate);

            completeProjectVos.add(complaintVo);
        });
        return completeProjectVos;
    }

    /**
     * 申诉统计-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<Complaint> exportGetComplaint(CompleteProjectDto dateDurationQueueIdDto) {
        // 获取申诉统计-任务分类维度
        List<Complaint> complaintList = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        switch (dateDurationQueueIdDto.getType()){
            case "0":
                complaintList = visitDefaultDao.getComplaintByTaskType(dateDurationQueueIdDto, accountingCenter);
                break;
            case "1":
                complaintList = visitDefaultDao.getComplaintByRegion(dateDurationQueueIdDto, accountingCenter);
                break;
            case "2":
                complaintList = visitDefaultDao.getComplaintByProductCategory(dateDurationQueueIdDto, accountingCenter);
                break;

        }

        return complaintList;
    }

    /**
     * 回访违约单
     *
     * @param visitDefaultDto
     * @param accountingCenter
     * @return
     */
    @Override
    public List<VisitDefaultVo> getVisitDefault(VisitDefaultDto visitDefaultDto, List<String> accountingCenter) {
        List<VisitDefaultVo> visitDefaultVos = visitDefaultDao.getVisitDefault(visitDefaultDto,accountingCenter);
        return visitDefaultVos;
    }

    /**
     * 回访坐席利用率
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<VisitRateVo> getVisitRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);

        List<VisitRateVo> visitRateVos = new ArrayList<>(localDateTimes.size());

        // 登录时长
        List<StartEndDateTimeDurationBo> loginBoList = dosCallCenterCheckStatsDao.selectStartEndDateTimeDuration(dateDurationQueueIdDto);

        // 回访完成量
        List<VisitCompleteNumber> visitCompleteNumbers  = dosCallCenterRecordDao.getBreatheNumber(dateDurationQueueIdDto);

        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalTime localTime = localDateTimes.get(i).toLocalTime();
            LocalDateTime intervalStart = localDateTimes.get(i);
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            int denominator = 0;
            // 分母累加登录时长
            denominator += getIntervalStateDuration(intervalStart, intervalEnd, loginBoList);
            VisitCompleteNumber visitCompleteNumber=new VisitCompleteNumber();
            if(j<visitCompleteNumbers.size()){
                visitCompleteNumber = visitCompleteNumbers.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalTime dbLocalTime = null;
            if(visitCompleteNumber.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(visitCompleteNumber.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER).toLocalTime();
            }

            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if ( dbLocalTime==null || localTime.isBefore(dbLocalTime)) {
                i++;
                VisitRateVo visitRateVo = VisitRateVo.builder()
                        .abscissa(localTime.toString())
                        .complete(ChartPointVo.builder().label("完成量").number(String.valueOf(0)).build())
                        .timeRate(ChartPointVo.builder().label("坐席效率").number(String.valueOf(0)).build())
                        .build();
                visitRateVos.add(visitRateVo);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                if(denominator!=0){
                    Double rate = Double.parseDouble(TimeUtil.getFourRate(denominator, 3600));
                    Double rate1 = Double.parseDouble(TimeUtil.getFourRate(rate, Double.parseDouble(11 + "")));
                    Double timeRate1 = Double.parseDouble(TimeUtil.getFourRate(Double.parseDouble(visitCompleteNumber.getComplete()), rate1));
                    Double timeRateDouble = timeRate1*30;
                    BigDecimal bd = new BigDecimal(timeRateDouble);
                    String timeRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                    VisitRateVo visitRateVo = VisitRateVo.builder()
                            .abscissa(localTime.toString())
                            .complete(ChartPointVo.builder().label("完成量").number(Optional.ofNullable(visitCompleteNumber.getComplete()).orElse(String.valueOf(0))).build())
                            .timeRate(ChartPointVo.builder().label("坐席效率").number(Optional.ofNullable(timeRate).orElse(String.valueOf(0))).build())
                            .build();
                    visitRateVos.add(visitRateVo);
                }else {
                    VisitRateVo visitRateVo = VisitRateVo.builder()
                            .abscissa(localTime.toString())
                            .complete(ChartPointVo.builder().label("完成量").number(Optional.ofNullable(visitCompleteNumber.getComplete()).orElse(String.valueOf(0))).build())
                            .timeRate(ChartPointVo.builder().label("坐席效率").number(String.valueOf(0)).build())
                            .build();
                    visitRateVos.add(visitRateVo);
                }
                i++;
                j++;
            }

        }

        return visitRateVos;
    }

    /**
     * 回访坐席利用率-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<VisitRate> exportGetVisitRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);
        List<VisitRate> visitRates = new ArrayList<>(localDateTimes.size());
        // 登录时长
        List<StartEndDateTimeDurationBo> loginBoList = dosCallCenterCheckStatsDao.selectStartEndDateTimeDuration(dateDurationQueueIdDto);
        // 回访完成量
        List<VisitCompleteNumber> visitCompleteNumbers  = dosCallCenterRecordDao.getBreatheNumber(dateDurationQueueIdDto);

        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localTime = localDateTimes.get(i);
            LocalDateTime intervalStart = localDateTimes.get(i);
            LocalDateTime intervalEnd = intervalStart.plusMinutes(30);
            int denominator = 0;
            // 分母累加登录时长
            denominator += getIntervalStateDuration(intervalStart, intervalEnd, loginBoList);
            VisitCompleteNumber visitCompleteNumber=new VisitCompleteNumber();
            if(j<visitCompleteNumbers.size()){
                visitCompleteNumber = visitCompleteNumbers.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalDateTime dbLocalTime = null;
            if(visitCompleteNumber.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(visitCompleteNumber.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER);
            }

            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalTime==null || localTime.isBefore(dbLocalTime)) {
                i++;
                VisitRate visitRate = VisitRate.builder()
                        .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .complete(String.valueOf(0))
                        .timeRate(String.valueOf(0))
                        .build();
                visitRates.add(visitRate);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                if(denominator!=0){
                    Double rate = Double.parseDouble(TimeUtil.getFourRate(denominator, 3600));
                    Double rate1 = Double.parseDouble(TimeUtil.getFourRate(rate, Double.parseDouble(11 + "")));
                    Double timeRate1 = Double.parseDouble(TimeUtil.getFourRate(Double.parseDouble(visitCompleteNumber.getComplete()), rate1));
                    Double timeRateDouble = timeRate1*30;
                    BigDecimal bd = new BigDecimal(timeRateDouble);
                    String timeRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                    VisitRate visitRate = VisitRate.builder()
                            .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                            .complete(Optional.ofNullable(visitCompleteNumber.getComplete()).orElse(String.valueOf(0)))
                            .timeRate(Optional.ofNullable(timeRate).orElse(String.valueOf(0)))
                            .build();
                    visitRates.add(visitRate);
                }else {
                    VisitRate visitRate = VisitRate.builder()
                            .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                            .complete(Optional.ofNullable(visitCompleteNumber.getComplete()).orElse(String.valueOf(0)))
                            .timeRate(String.valueOf(0))
                            .build();
                    visitRates.add(visitRate);
                }

                i++;
                j++;
            }


        }

        return visitRates;
    }

    /**
     * 违约率
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<DefaultRateVo> getDefaultRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        //把String转为LocalDate
        LocalDate localTime=LocalDate.parse(dateDurationQueueIdDto.getEndDate(),DatePattern.NORM_DATE_FORMATTER);
        //判断当前日期是否大于指定日期
        if(!LocalDate.now().isAfter(localTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateDurationQueueIdDto.setEndDate(sdf.format(new Date()));
        }
        List<String> timeList = TimeUtil.findDaysStr(dateDurationQueueIdDto.getStartDate(), dateDurationQueueIdDto.getEndDate(), "0");
        // 获取每个时间段的违约率Bo
        List<DefaultRate> defaultRates = new ArrayList<>();
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        defaultRates = visitDefaultDao.getDefaultRate(dateDurationQueueIdDto,accountingCenter);
        List<DefaultRateVo> defaultRateVos = new ArrayList<>(timeList.size());
        int i = 0, j = 0;
        while (i < timeList.size()) {
            LocalDate localDate = LocalDate.parse(timeList.get(i),DatePattern.NORM_DATE_FORMATTER);
            DefaultRate defaultRate=new DefaultRate();
            if(j<defaultRates.size()){
                defaultRate = defaultRates.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalDate dbLocalDate=null;

            if(defaultRate.getAbscissa()!=null){
                dbLocalDate = LocalDate.parse(defaultRate.getAbscissa(), DatePattern.NORM_DATE_FORMATTER);
            }



            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalDate==null ||localDate.isBefore(dbLocalDate)) {
                i++;
                DefaultRateVo defaultRateVo = DefaultRateVo.builder()
                        .abscissa(localDate.toString())
                        .completeDefaultRate(ChartPointVo.builder().label("回访完成违约率").number(String.valueOf(0)).build())
                        .auditDefaultRate(ChartPointVo.builder().label("回访审核违约率").number(String.valueOf(0)).build())
                        .build();
                defaultRateVos.add(defaultRateVo);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localDate.isAfter(dbLocalDate)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localDate.equals(dbLocalDate)) {
                DefaultRateVo defaultRateVo = DefaultRateVo.builder()
                        .abscissa(localDate.toString())
                        .completeDefaultRate(ChartPointVo.builder().label("回访完成违约率").number(Optional.ofNullable(defaultRate.getCompleteDefaultRate()).orElse(String.valueOf(0))).build())
                        .auditDefaultRate(ChartPointVo.builder().label("回访审核违约率").number(Optional.ofNullable(defaultRate.getAuditDefaultRate()).orElse(String.valueOf(0))).build())
                        .build();
                defaultRateVos.add(defaultRateVo);
                i++;
                j++;
            }

        }
        return defaultRateVos;
    }

    /**
     * 违约率-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<DefaultRate> exportGetDefaultRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 根据队列id获取核算中心
        List<String> accountingCenter = visitAuditDao.getQueueDeptList(dateDurationQueueIdDto.getQueueId());
        // 工作时间段
        //把String转为LocalDate
        LocalDate localTime=LocalDate.parse(dateDurationQueueIdDto.getEndDate(),DatePattern.NORM_DATE_FORMATTER);
        //判断当前日期是否大于指定日期
        if(!LocalDate.now().isAfter(localTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateDurationQueueIdDto.setEndDate(sdf.format(new Date()));
        }
        List<String> timeList = TimeUtil.findDaysStr(dateDurationQueueIdDto.getStartDate(), dateDurationQueueIdDto.getEndDate(), "0");
        // 获取每个时间段的违约率Bo
        List<DefaultRate> defaultRates = new ArrayList<>();
        List<DefaultRate> newDefaultRate = new ArrayList<>();
        defaultRates = visitDefaultDao.getDefaultRate(dateDurationQueueIdDto,accountingCenter);
        List<DefaultRateVo> defaultRateVos = new ArrayList<>(timeList.size());
        int i = 0, j = 0;
        while (i < timeList.size()) {
            LocalDate localDate = LocalDate.parse(timeList.get(i),DatePattern.NORM_DATE_FORMATTER);
            DefaultRate defaultRate=new DefaultRate();
            if(j<defaultRates.size()){
                defaultRate = defaultRates.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalDate dbLocalDate=null;

            if(defaultRate.getAbscissa()!=null){
                dbLocalDate = LocalDate.parse(defaultRate.getAbscissa(), DatePattern.NORM_DATE_FORMATTER);
            }



            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalDate==null ||localDate.isBefore(dbLocalDate)) {
                i++;
                DefaultRate defaultRateVo = DefaultRate.builder()
                        .abscissa(localDate.toString())
                        .completeDefaultRate(String.valueOf(0))
                        .auditDefaultRate(String.valueOf(0))
                        .build();
                newDefaultRate.add(defaultRateVo);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localDate.isAfter(dbLocalDate)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localDate.equals(dbLocalDate)) {
                DefaultRate defaultRateVo = DefaultRate.builder()
                        .abscissa(localDate.toString())
                        .completeDefaultRate(defaultRate.getCompleteDefaultRate())
                        .auditDefaultRate(defaultRate.getAuditDefaultRate())
                        .build();
                newDefaultRate.add(defaultRateVo);
                i++;
                j++;
            }

        }
        return newDefaultRate;
    }

    /**
     * 回访呼出量/呼通量
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<BreatheRateVo> getBreatheRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);

        // 获取每个时间段的回访呼通率
        List<BreatheRate> breatheRates = dosCallCenterRecordDao.getBreatheRate(dateDurationQueueIdDto);

        List<BreatheRateVo> breatheRateVos = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalTime localTime = localDateTimes.get(i).toLocalTime();

            BreatheRate breatheRate=new BreatheRate();
            if(j<breatheRates.size()){
                breatheRate = breatheRates.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalTime dbLocalTime=null;
            if(breatheRate.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(breatheRate.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER).toLocalTime();
            }



            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalTime==null ||localTime.isBefore(dbLocalTime)) {
                i++;
                BreatheRateVo breatheRateVo = BreatheRateVo.builder()
                        .abscissa(localTime.toString())
                        .breatheOut(ChartPointVo.builder().label("呼出量").number(String.valueOf(0)).build())
                        .callFlux(ChartPointVo.builder().label("呼通量").number(String.valueOf(0)).build())
                        .breatheRate(ChartPointVo.builder().label("呼通率").number(String.valueOf(0)).build())
                        .build();
                breatheRateVos.add(breatheRateVo);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                BreatheRateVo breatheRateVo = BreatheRateVo.builder()
                        .abscissa(localTime.toString())
                        .breatheOut(ChartPointVo.builder().label("呼出量").number(Optional.ofNullable(breatheRate.getBreatheOut()).orElse(String.valueOf(0))).build())
                        .callFlux(ChartPointVo.builder().label("呼通量").number(Optional.ofNullable(breatheRate.getCallFlux()).orElse(String.valueOf(0))).build())
                        .breatheRate(ChartPointVo.builder().label("呼通率").number(Optional.ofNullable(breatheRate.getBreatheRate()).orElse(String.valueOf(0))).build())
                        .build();
                breatheRateVos.add(breatheRateVo);
                i++;
                j++;
            }

        }
        return breatheRateVos;
    }

    /**
     * 回访呼出量/呼通量-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<BreatheRate> exportGetBreatheRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);

        // 获取每个时间段的回访呼通率
        List<BreatheRate> breatheRates = dosCallCenterRecordDao.getBreatheRate(dateDurationQueueIdDto);

        List<BreatheRate> newBreathRate = new ArrayList<>();
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localTime = localDateTimes.get(i);

            BreatheRate breatheRate=new BreatheRate();
            if(j<breatheRates.size()){
                breatheRate = breatheRates.get(j);
            }

            // 获取数据库查询的数据的时间
            LocalDateTime dbLocalTime=null;
            if(breatheRate.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(breatheRate.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER);
            }

            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalTime==null || localTime.isBefore(dbLocalTime)) {
                i++;
                BreatheRate newBreath = BreatheRate.builder()
                        .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .breatheOut(String.valueOf(0))
                        .callFlux(String.valueOf(0))
                        .breatheRate(String.valueOf(0))
                        .build();
                newBreathRate.add(newBreath);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                BreatheRate newBreath = BreatheRate.builder()
                        .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .breatheOut(Optional.ofNullable(breatheRate.getBreatheOut()).orElse(String.valueOf(0)))
                        .callFlux(Optional.ofNullable(breatheRate.getCallFlux()).orElse(String.valueOf(0)))
                        .breatheRate(Optional.ofNullable(breatheRate.getBreatheRate()).orElse(String.valueOf(0)))
                        .build();
                newBreathRate.add(newBreath);
                i++;
                j++;
            }


        }
        return newBreathRate;
    }

    /**
     * 回访坐席满意度
     * @param dateDurationQueueIdDto
     * @return
     */
    @Override
    public List<SatisfactionRateVo> getSatisfactionRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);

        // 获取每个时间段的回访呼通率
        List<SatisfactionRate> satisfactionRateList = dosCallCenterRecordDao.getSatisfactionRate(dateDurationQueueIdDto);

        List<SatisfactionRateVo> satisfactionRateVoArrayList = new ArrayList<>(localDateTimes.size());
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalTime localTime = localDateTimes.get(i).toLocalTime();
            SatisfactionRate satisfactionRate=new SatisfactionRate();
            if(j<satisfactionRateList.size()){
                satisfactionRate = satisfactionRateList.get(j);
            }
            // 获取数据库查询的数据的时间
            LocalTime dbLocalTime=null;
            if(satisfactionRate.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(satisfactionRate.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER).toLocalTime();
            }


            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalTime==null ||localTime.isBefore(dbLocalTime)) {
                i++;
                SatisfactionRateVo satisfactionRateVo = SatisfactionRateVo.builder()
                        .abscissa(localTime.toString())
                        .satisfactionRate(ChartPointVo.builder().label("满意率").number(String.valueOf(0)).build())
                        .build();
                satisfactionRateVoArrayList.add(satisfactionRateVo);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                SatisfactionRateVo satisfactionRateVo = SatisfactionRateVo.builder()
                        .abscissa(localTime.toString())
                        .satisfactionRate(ChartPointVo.builder().label("满意率").number(Optional.ofNullable(satisfactionRate.getSatisfactionRate()).orElse(String.valueOf(0))).build())
                        .build();
                satisfactionRateVoArrayList.add(satisfactionRateVo);
                i++;
                j++;
            }


        }
        return satisfactionRateVoArrayList;
    }

    /**
     * 回访坐席满意度-导出
     * @param dateDurationQueueIdDto
     * @return
     */
    public List<SatisfactionRate> exportGetSatisfactionRate(DateDurationQueueIdDto dateDurationQueueIdDto) {
        // 工作时间段
        List<LocalDateTime> localDateTimes = commonService.getWorkTimeIntervalByDateDuration(LocalDate.parse(dateDurationQueueIdDto.getStartDate()), LocalDate.parse(dateDurationQueueIdDto.getEndDate()), ChronoUnit.MINUTES, 30);

        // 获取每个时间段的回访呼通率
        List<SatisfactionRate> satisfactionRateList = dosCallCenterRecordDao.getSatisfactionRate(dateDurationQueueIdDto);

        List<SatisfactionRate> newSatisfactionRates = new ArrayList<>();
        int i = 0, j = 0;
        while (i < localDateTimes.size()) {
            LocalDateTime localTime = localDateTimes.get(i);
            SatisfactionRate satisfactionRate=new SatisfactionRate();
            if(j<satisfactionRateList.size()){
                satisfactionRate = satisfactionRateList.get(j);
            }
            // 获取数据库查询的数据的时间
            LocalDateTime dbLocalTime=null;
            if(satisfactionRate.getAbscissa()!=null){
                dbLocalTime = LocalDateTime.parse(satisfactionRate.getAbscissa(), DatePattern.NORM_DATETIME_FORMATTER);
            }

            // 时间列表的时间<数据库查出的数据的时间 列表时间往后找 把值设为0
            if (dbLocalTime==null || localTime.isBefore(dbLocalTime)) {
                i++;
                SatisfactionRate newSatisfaction = SatisfactionRate.builder()
                        .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .satisfactionRate(String.valueOf(0))
                        .build();
                newSatisfactionRates.add(newSatisfaction);
                continue;
            }

            // 时间列表的时间>数据库查出的数据的时间 数据库数据往后找
            if (localTime.isAfter(dbLocalTime)) {
                j++;
                continue;
            }

            // 数据库查出的数据的时间=时间列表的时间 获取率放到Vo
            if (localTime.equals(dbLocalTime)) {
                SatisfactionRate newSatisfaction = SatisfactionRate.builder()
                        .abscissa(localTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                        .satisfactionRate(Optional.ofNullable(satisfactionRate.getSatisfactionRate()).orElse(String.valueOf(0)))
                        .build();
                newSatisfactionRates.add(newSatisfaction);
                i++;
                j++;
            }


        }
        return newSatisfactionRates;
    }

    @Override
    public List<VisitAgentStateTableVo> visitAgentStateTable(VisitTableDto dto, String historyFlag) {
        // 获取回访坐席坐席状态
        List<VisitAgentStateTableVo> visitAgentStateTable = dosCallCenterCheckStatsDao.getVisitAgentStateTable(dto,historyFlag);

        List<SeatStatusReminder> seatStatusReminders = ccBaseDataDao.selectSeatStatusReminder();
        Map<String, String> seatStatusTimeOutMap = new HashMap<>(8);
        seatStatusReminders.forEach(status -> seatStatusTimeOutMap.put(status.getStatusName(), status.getTimeout()));
        for (VisitAgentStateTableVo vo : visitAgentStateTable) {
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
        int j=0;
        for (int i = 0; i < visitAgentStateTable.size(); i++) {
            if(visitAgentStateTable.get(i).getCurrentStateTimeOutFlag()==1){
                VisitAgentStateTableVo temp = visitAgentStateTable.get(i);
                visitAgentStateTable.set(i, visitAgentStateTable.get(j));
                visitAgentStateTable.set(j, temp);
                j++;
            }
        }
        return visitAgentStateTable;
    }

    @Override
    public void exportChart(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        // 已完成回访项目-任务分类维度-导出
        if("0".equals(exportConditionDto.getExportType())){
            exportCompleteProject(response,exportConditionDto);
        // 已完成回访项目-任务分类维度-灰色-导出
        } else if ("1".equals(exportConditionDto.getExportType())) {
            exportCompleteProjectByGray(response,exportConditionDto);
        // 申诉统计-导出
        }else if ("2".equals(exportConditionDto.getExportType())) {
            exportComplaint(response,exportConditionDto);
        // 回访坐席利用率
        }else if ("3".equals(exportConditionDto.getExportType())) {
            exportVisitRate(response,exportConditionDto);
        // 违约率-导出
        }else if ("4".equals(exportConditionDto.getExportType())) {
            exportDefaultRate(response,exportConditionDto);
        // 回访呼出量/呼通量-导出
        }else if ("5".equals(exportConditionDto.getExportType())) {
            exportBreatheRate(response,exportConditionDto);
        // 回访坐席满意度-导出
        }else if ("6".equals(exportConditionDto.getExportType())) {
            exportSatisfactionRate(response,exportConditionDto);
        // 工时利用率-导出
        } else if ("7".equals(exportConditionDto.getExportType())) {
            HotLineExportConditionDto hotLineExportConditionDto = HotLineExportConditionDto.builder()
                    .startDate(exportConditionDto.getStartDate())
                    .endDate(exportConditionDto.getEndDate())
                    .queueId(exportConditionDto.getQueueId()).build();
            hotOrderFollowProcessService.agentManHourUtilizationRateChartExport(response,hotLineExportConditionDto,"out");
        }
    }

    /**
     * 回访坐席满意度-导出
     * @param response
     * @param exportConditionDto
     */
    private void exportSatisfactionRate(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        DateDurationQueueIdDto dateDurationQueueIdDto = DateDurationQueueIdDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<SatisfactionRate> satisfactionRateList = exportGetSatisfactionRate(dateDurationQueueIdDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("abscissa", "时间");
        headerMap.put("satisfactionRate", "满意率");
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
        ExcelTool excelTool  = new ExcelTool("回访坐席满意度"+operationTime+".xlsx",20,20, null, "回访坐席满意度列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, satisfactionRateList,true);
        } catch (Exception e) {
            LOGGER.error("回访坐席满意度导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 回访呼出量/呼通量-导出
     * @param response
     * @param exportConditionDto
     */
    private void exportBreatheRate(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        DateDurationQueueIdDto dateDurationQueueIdDto = DateDurationQueueIdDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<BreatheRate> breatheRates = exportGetBreatheRate(dateDurationQueueIdDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("abscissa", "时间");
        headerMap.put("breatheOut", "呼出量");
        headerMap.put("callFlux", "呼通量");
        headerMap.put("breatheRate", "呼通率");
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
        ExcelTool excelTool  = new ExcelTool("回访呼出量-呼通量"+operationTime+".xlsx",20,20, null, "回访呼出量-呼通量列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, breatheRates,true);
        } catch (Exception e) {
            LOGGER.error("回访呼出量/呼通量导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 违约率-导出
     * @param response
     * @param exportConditionDto
     */
    private void exportDefaultRate(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        DateDurationQueueIdDto dateDurationQueueIdDto = DateDurationQueueIdDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<DefaultRate> defaultRates = exportGetDefaultRate(dateDurationQueueIdDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("abscissa", "时间");
        headerMap.put("completeDefaultRate", "回访完成违约率");
        headerMap.put("auditDefaultRate", "回访审核违约率");
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
        ExcelTool excelTool  = new ExcelTool("违约率"+operationTime+".xlsx",20,20, null, "违约率列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, defaultRates,true);
        } catch (Exception e) {
            LOGGER.error("违约率导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 回访坐席利用率
     * @param response
     * @param exportConditionDto
     */
    private void exportVisitRate(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        DateDurationQueueIdDto dateDurationQueueIdDto = DateDurationQueueIdDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<VisitRate> visitRates = exportGetVisitRate(dateDurationQueueIdDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("abscissa", "时间");
        headerMap.put("complete", "完成量");
        headerMap.put("timeRate", "工时利用率");
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
        ExcelTool excelTool  = new ExcelTool("回访坐席利用率"+operationTime+".xlsx",20,20, null, "回访坐席利用率列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, visitRates,true);
        } catch (Exception e) {
            LOGGER.error("回访坐席利用率导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 申诉统计-导出
     * @param response
     * @param exportConditionDto
     */
    public void exportComplaint(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        CompleteProjectDto completeProjectDto = CompleteProjectDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .type(exportConditionDto.getType())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<Complaint> complaints = exportGetComplaint(completeProjectDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        // 0任务分类维度 1 大区维度 2 产品品类维度 3 坐席维度
        if("0".equals(exportConditionDto.getType())){
            headerMap.put("abscissa", "任务分类");
        } else if ("1".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "大区");
        } else if ("2".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "产品品类");
        }
        headerMap.put("complaintRate", "申诉率");
        headerMap.put("noComplaintRate", "申诉不通过率");
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
        ExcelTool excelTool  = new ExcelTool("申诉统计"+operationTime+".xlsx",20,20, null, "申诉统计列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, complaints,true);
        } catch (Exception e) {
            LOGGER.error("申诉统计导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 已完成回访项目-任务分类维度-灰色-导出
     * @param response
     * @param exportConditionDto
     */
    public void exportCompleteProjectByGray(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        CompleteProjectDto completeProjectDto = CompleteProjectDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .type(exportConditionDto.getType())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<GrayProject> grayProjects = exportGetCompleteProjectByGray(completeProjectDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        // 0任务分类维度 1 大区维度 2 产品品类维度 3 坐席维度
        if("0".equals(exportConditionDto.getType())){
            headerMap.put("abscissa", "任务分类");
        } else if ("1".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "大区");
        } else if ("2".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "产品品类");
        } else if ("3".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "坐席");
        }
        headerMap.put("total", "数量");
        headerMap.put("notKnow", "不知情");
        headerMap.put("notKnowRate", "不知情占比");
        headerMap.put("noComplete", "未完成");
        headerMap.put("noCompleteRate", "未完成占比");
        headerMap.put("noAnswering", "无人接听");
        headerMap.put("noAnsweringRate", "无人接听占比");
        headerMap.put("downtime", "停机");
        headerMap.put("downtimeRate", "停机占比");
        headerMap.put("reject", "拒接");
        headerMap.put("rejectRate", "拒接占比");
        headerMap.put("refusedVisit", "拒访");
        headerMap.put("refusedVisitRate", "拒访占比");
        headerMap.put("gaiHao", "改号");
        headerMap.put("gaiHaoRate", "改号占比");
        headerMap.put("turnOff", "关机");
        headerMap.put("turnOffRate", "关机占比");
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
        ExcelTool excelTool = new ExcelTool();
        if("3".equals(exportConditionDto.getType())){
            // 添加行内数据
            excelTool = new ExcelTool("坐席回访结果统计"+operationTime+".xlsx",20,20, null, "坐席回访结果统计列表");
        }else {
            // 添加行内数据
            excelTool = new ExcelTool("已完成回访项目灰色"+operationTime+".xlsx",20,20, null, "已完成回访项目灰色列表");
        }

        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, grayProjects,true);
        } catch (Exception e) {
            LOGGER.error("已完成回访项目灰色导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 已完成回访项目-任务分类维度-导出
     * @param response
     * @param exportConditionDto
     */
    public void exportCompleteProject(HttpServletResponse response, ExportConditionDto exportConditionDto) {
        CompleteProjectDto completeProjectDto = CompleteProjectDto.builder()
                .startDate(exportConditionDto.getStartDate())
                .endDate(exportConditionDto.getEndDate())
                .type(exportConditionDto.getType())
                .queueId(exportConditionDto.getQueueId())
                .build();
        List<CompleteProject> completeProjects = exportGetCompleteProject(completeProjectDto);
        //设置表头
        Map<String, String> headerMap = new LinkedHashMap<>();
        // 0任务分类维度 1 大区维度 2 产品品类维度 3 坐席维度
        if("0".equals(exportConditionDto.getType())){
            headerMap.put("abscissa", "任务分类");
        } else if ("1".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "大区");
        } else if ("2".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "产品品类");
        } else if ("3".equals(exportConditionDto.getType())) {
            headerMap.put("abscissa", "坐席");
        }
        headerMap.put("total", "数量");
        headerMap.put("complete", "完成数量");
        headerMap.put("completeRate", "完成数量占比");
        headerMap.put("gray", "灰色数量");
        headerMap.put("grayRate", "灰色数量占比");
        headerMap.put("defaultCount", "违约");
        headerMap.put("defaultCountRate", "违约占比");
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
        ExcelTool excelTool = new ExcelTool("已完成回访项目"+operationTime+".xlsx",20,20, null, "已完成回访项目列表");
        try {
            List<Column> titleData = excelTool.columnTransformer(titleList,"t_id","t_pid","t_content",
                    "t_fielName", pid);
            excelTool.exportWorkbook(excelTool.getTitle(), response, titleData, completeProjects,true);
        } catch (Exception e) {
            LOGGER.error("已完成回访项目导出失败", e);
            e.printStackTrace();
            throw new BusinessException("导出失败");
        }
    }

    /**
     * 各状态持续时长
     *
     * @param intervalStart  时段开始时间
     * @param intervalEnd    时段结束时间
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

            // 开始时间在时段内
            if (startDateTime.isAfter(intervalStart) && startDateTime.isBefore(intervalEnd)) {
                // 无结束时间或结束时间在时段外
                if (endDateTime == null || endDateTime.isAfter(intervalEnd)) {
                    duration += Duration.between(startDateTime, intervalEnd).getSeconds();
                } else if (endDateTime.isAfter(intervalStart) && endDateTime.isBefore(intervalEnd)) {
                    // 结束时间在时段内
                    duration += startEndDateTimeDurationBo.getDuration();
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
        }
        return (int) duration;
    }
}
