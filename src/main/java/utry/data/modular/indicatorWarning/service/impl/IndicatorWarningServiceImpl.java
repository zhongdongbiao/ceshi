package utry.data.modular.indicatorWarning.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.core.websocket.bo.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.complaints.dao.ComplaintsDao;
import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.indicatorWarning.constant.ProjectConstant;
import utry.data.modular.indicatorWarning.dao.IndicatorWarningDao;
import utry.data.modular.indicatorWarning.dto.AssumeUserDto;
import utry.data.modular.indicatorWarning.dto.IndicatorAnomalyWarningDto;
import utry.data.modular.indicatorWarning.dto.MailDto;
import utry.data.modular.indicatorWarning.service.IndicatorWarningService;
import utry.data.modular.indicatorWarning.vo.IndicatorAnomalyWarningVo;
import utry.data.modular.partsManagement.dao.InventoryWarningDao;
import utry.data.modular.partsManagement.dao.ReceiptDao;
import utry.data.modular.partsManagement.service.impl.CoreIndexServiceImpl;
import utry.data.util.MessageUtil;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 16:37
 * description 指标预警的实现类
 */
@Service
public class IndicatorWarningServiceImpl implements IndicatorWarningService {

    Logger logger = LoggerFactory.getLogger(IndicatorWarningServiceImpl.class);

    @Resource
    private IndicatorWarningDao indicatorWarningDao;

    @Resource
    private CoreIndexServiceImpl  coreIndexServiceImpl;

    @Resource
    private InventoryWarningDao inventoryWarningDao;

    @Resource
    private ReceiptDao receiptDao;

    @Resource
    private ComplaintsDao complaintsDao;

    @Resource
    private SysConfServiceImpl sysConfService;

    @Override
    public void stationLetter(List<Map<String, String>> paramMap) {
        for (Map<String, String> map : paramMap) {
            String complaintNumber = map.get("complaintNumber");
            String departmentNumber = map.get("departmentNumber");
            if (departmentNumber != null && complaintNumber != null) {
                String relationProject = indicatorWarningDao.selectRelationProject(departmentNumber);
                if (relationProject == null) {
                    relationProject = "";
                }
                // 获取零件管理所有的担当用户信息
                List<AssumeUserDto> allAssumeList;
                switch (relationProject) {
                    case ProjectConstant.DISTRICT:
                        allAssumeList = indicatorWarningDao.getDistrictAllAssume();
                        break;
                    case ProjectConstant.PARTS_MANAGEMENT:
                        allAssumeList = indicatorWarningDao.getPartsManagementAllAssume();
                        break;
                    case ProjectConstant.TECHNICAL_QUALITY:
                        allAssumeList = indicatorWarningDao.getTechnicalQualityAllAssume();
                        break;
                    case ProjectConstant.COMPLAINT:
                        allAssumeList = indicatorWarningDao.getComplaintAllAssume();
                        break;
                    default:
                        allAssumeList = null;
                        break;
                }
                List<String> accountIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(allAssumeList)) {
                    accountIdList = allAssumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
                }
                MailDto stationLetter = indicatorWarningDao.getStationLetter(complaintNumber);
                if (accountIdList != null && accountIdList.size() > 0) {
                    List<UserInfo> userInfos = indicatorWarningDao.getUserInfoByAccountId(accountIdList);
                    if (CollectionUtils.isNotEmpty(userInfos)) {
                        String sendContext = "投诉处理单：" + stationLetter.getComplaintNumber() + "待处理 - " + stationLetter.getComplaintStartTime();
                        MessageUtil.send("投诉处理", sendContext, "auto", userInfos);
                    }
                }
            }
        }
    }

    /**
     * 3小时一次预警的指标
     * @return
     */
    @Override
    public void threeHoursOneWarning() {
        List<AssumeUserDto> districtAllAssumeList = indicatorWarningDao.getDistrictAllAssume();
        List<String> accountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(districtAllAssumeList)) {
            accountIdList = districtAllAssumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }
        // 大区服务 - 核心指标30分钟及时预约率
        IndicatorAnomalyWarningVo judgeTimelyWarningVo = judgeTimely(null, null, null);
        if (judgeTimelyWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeTimelyWarningVo, accountIdList);
        }
        // 大区服务 - 核心指标首次预约准时上门率
        IndicatorAnomalyWarningVo firstJudgePunctualityWarningVo = judgePunctuality(null, null, null, "首次预约");
        if (firstJudgePunctualityWarningVo.getWarningCopyWriting() != null) {
            sendMessage(firstJudgePunctualityWarningVo, accountIdList);
        }
        // 大区服务 - 核心指标非首次预约准时上门率
        IndicatorAnomalyWarningVo secondJudgePunctualityWarningVo = judgePunctuality(null, null, null, "预约");
        if (secondJudgePunctualityWarningVo.getWarningCopyWriting() != null) {
            sendMessage(secondJudgePunctualityWarningVo, accountIdList);
        }
        // 大区服务 - 核心指标TAT平均服务完成时长
        IndicatorAnomalyWarningVo judgeAverageWarningVo = judgeAverage(null, null, null);
        if (judgeAverageWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeAverageWarningVo, accountIdList);
        }
    }

    /**
     * 一天一次指标预警-零件管理
     * @return
     */
    @Override
    public void oneDayOneWarningPartsManagement() {
        try {
            this.partsManagement();
        } catch (Exception e) {
            logger.info("指标预警-零件管理：", e);
        }
    }

    /**
     * 一天一次指标预警-大区服务
     */
    @Override
    public void oneDayOneWarningDistrict() {
        try {
            this.district();
        } catch (Exception e) {
            logger.info("指标预警-大区服务：", e);
        }
    }

    /**
     * 一天一次指标预警-投诉处理
     */
    @Override
    public void oneDayOneWarningComplaint() {
        try {
            this.complaint();
        } catch (Exception e) {
            logger.info("指标预警-投诉处理：", e);
        }
    }

    /**
     * 一天一次指标预警-技术品质
     */
    @Override
    public void oneDayOneWarningCategory() {
        try {
            this.category();
        } catch (Exception e) {
            logger.info("指标预警-技术品质：", e);
        }
    }

    /**
     * 零件管理部分
     * @return
     */
    public void partsManagement() {
        // 获取系统配置的高级参数
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        // 获取零件管理所有的担当用户信息
        List<AssumeUserDto> partsManagementAllAssumeList = indicatorWarningDao.getPartsManagementAllAssume();
        List<String> accountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(partsManagementAllAssumeList)) {
            accountIdList = partsManagementAllAssumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }
        Map<String, String> assumeMap = new HashMap<>(4);
        assumeMap.put("businessCode", "partManagement");
        assumeMap.put("month", month);
        List<AssumeUserDto> assumeList = indicatorWarningDao.getResponsibilityAssume(assumeMap);
        List<String> assumeAccountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assumeList)) {
            assumeAccountIdList = assumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }
        // 获取所有超过三天未妥投的订单
        for (String userId : assumeAccountIdList) {
            List<IndicatorAnomalyWarningDto> transitPassThreeDays = indicatorWarningDao.getTransitPassThreeDays(userId, inventoryDate);
            for (IndicatorAnomalyWarningDto transitPassThreeDay : transitPassThreeDays) {
                IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
                warningVo.setWarningValue("收货单三天内未签收");
                warningVo.setWarningCopyWriting("收货单：" + transitPassThreeDay.getDocumentNumber() + "已" + transitPassThreeDay.getTransitDays() + "天未妥投");
                warningVo.setDocumentNumber(transitPassThreeDay.getDocumentNumber());
                sendMessage(warningVo, Collections.singletonList(userId));
            }
        }
        String orderType = "作业订单";
        List<IndicatorAnomalyWarningDto> circulationPassThreeDays = indicatorWarningDao.getCirculationPassThreeDays(orderType);
        for (IndicatorAnomalyWarningDto circulationPassThreeDay : circulationPassThreeDays) {
            IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
            warningVo.setWarningValue("服务店作业单流转天数超过3天");
            String warningCopyWriting = "服务店作业单：" + circulationPassThreeDay.getDocumentNumber() + "已流转"
                    + circulationPassThreeDay.getCirculationDays() + "天";
            warningVo.setWarningCopyWriting(warningCopyWriting);
            warningVo.setDocumentNumber(circulationPassThreeDay.getDocumentNumber());
            sendMessage(warningVo, assumeAccountIdList);
        }
        for (String userId : assumeAccountIdList) {
            List<IndicatorAnomalyWarningDto> inventoryWarnings = indicatorWarningDao.getInventoryWarning(userId);
            for (IndicatorAnomalyWarningDto inventoryWarning : inventoryWarnings) {
                IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
                warningVo.setWarningValue("部品低于最小安全在库");
                String warningCopyWriting = "部品：" + inventoryWarning.getDescribedDrawingNo()
                        + "（" + inventoryWarning.getPartDrawingNumber() + "）主板即将缺货 剩余"
                        + inventoryWarning.getCurrentInventory() + "件";
                if (inventoryWarning.getArrivalDays() != null && inventoryWarning.getArrivalDays() >= 0) {
                    warningCopyWriting = warningCopyWriting + " 下批预计" + inventoryWarning.getArrivalDays()
                            + "天到货";
                }
                warningVo.setWarningCopyWriting(warningCopyWriting);
                sendMessage(warningVo, Collections.singletonList(userId));
            }
        }

        // 零件管理 - 核心指标的部品出货即纳率
        IndicatorAnomalyWarningVo shipmentWarningVo = judgeShipment(null, null, null);
        if (shipmentWarningVo.getWarningCopyWriting() != null) {
            sendMessage(shipmentWarningVo, accountIdList);
        }
        // 零件管理 - 核心指标的在库金额
        IndicatorAnomalyWarningVo stockAmountWarningVo = judgeStockAmount(null, null, null);
        if (stockAmountWarningVo.getWarningValue() != null) {
            sendMessage(stockAmountWarningVo, accountIdList);
        }
        // 零件管理 - 核心指标的NDS2
        IndicatorAnomalyWarningVo nds2WarningVo = judgeNds2(null, null, null);
        if (nds2WarningVo.getWarningValue() != null) {
            sendMessage(nds2WarningVo, accountIdList);
        }
        // 零件管理 - 部品出货即纳率的担当指标
        Map<String, String> partImmediateMap = new HashMap<>(4);
        partImmediateMap.put("businessCode", "partManagement");
        partImmediateMap.put("month", month);
        partImmediateMap.put("indicatorCode", "partImmediate");
        List<AssumeUserDto> partImmediateAssumeList = indicatorWarningDao.getResponsibilityAssume(partImmediateMap);
        for (AssumeUserDto partImmediateAssume : partImmediateAssumeList) {
            IndicatorAnomalyWarningVo judgeShipmentWarningVo = judgeShipment(partImmediateAssume.getUserId(), partImmediateAssume.getRealName(), partImmediateAssume.getIndicatorValue());
            if (judgeShipmentWarningVo.getWarningCopyWriting() != null) {
                sendMessage(judgeShipmentWarningVo, Collections.singletonList(partImmediateAssume.getUserId()));
            }
        }
        // 零件管理 - 在库金额的担当指标
        Map<String, String> stockAmountMap = new HashMap<>(4);
        stockAmountMap.put("businessCode", "partManagement");
        stockAmountMap.put("month", month);
        stockAmountMap.put("indicatorCode", "stockAmount");
        List<AssumeUserDto> stockAmountAssumeList = indicatorWarningDao.getResponsibilityAssume(stockAmountMap);
        for (AssumeUserDto stockAmountAssume : stockAmountAssumeList) {
            IndicatorAnomalyWarningVo judgeStockAmountWarningVo = judgeStockAmount(stockAmountAssume.getUserId(), stockAmountAssume.getRealName(), stockAmountAssume.getIndicatorValue());
            if (judgeStockAmountWarningVo.getWarningCopyWriting() != null) {
                sendMessage(judgeStockAmountWarningVo, Collections.singletonList(stockAmountAssume.getUserId()));
            }
        }
        // 零件管理 - NDS2的担当指标
        Map<String, String> nds2Map = new HashMap<>(4);
        nds2Map.put("businessCode", "partManagement");
        nds2Map.put("month", month);
        nds2Map.put("indicatorCode", "nds2");
        List<AssumeUserDto> nds2AssumeList = indicatorWarningDao.getResponsibilityAssume(nds2Map);
        for (AssumeUserDto nds2Assume : nds2AssumeList) {
            IndicatorAnomalyWarningVo judgeNds2WarningVo = judgeNds2(nds2Assume.getUserId(), nds2Assume.getRealName(), nds2Assume.getIndicatorValue());
            if (judgeNds2WarningVo.getWarningCopyWriting() != null) {
                sendMessage(judgeNds2WarningVo, Collections.singletonList(nds2Assume.getUserId()));
            }
        }
    }

    /**
     * 大区服务
     * @return
     */
    public void district() {
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        List<AssumeUserDto> districtAllAssumeList = indicatorWarningDao.getDistrictAllAssume();
        List<String> accountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(districtAllAssumeList)) {
            accountIdList = districtAllAssumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }

        // 大区服务 - 核心指标投诉7天解决率
        IndicatorAnomalyWarningVo judgeSolveWarningVo = judgeSolve(null, null, null);
        if (judgeSolveWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeSolveWarningVo, accountIdList);
        }
        // 大区服务 - 核心指标一次修复率小于阈值
        IndicatorAnomalyWarningVo judgeRepairWarningVo = judgeRepair(null, null, null);
        if (judgeRepairWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeRepairWarningVo, accountIdList);
        }

        // 大区服务 - 核心指标N+1投诉解决方案提交率
        IndicatorAnomalyWarningVo judgeSchemeWarningVo = judgeScheme(null, null, null);
        if (judgeSchemeWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeSchemeWarningVo, accountIdList);
        }
        // 大区服务 - 担当指标30分钟及时预约率
        Map<String, String> timelyRatwMap = new HashMap<>(4);
        timelyRatwMap.put("businessCode", "district");
        timelyRatwMap.put("month", month);
        timelyRatwMap.put("indicatorCode", "timelyRatw");
        List<AssumeUserDto> timelyRatwAssumeList = indicatorWarningDao.getResponsibilityAssume(timelyRatwMap);
        for (AssumeUserDto timelyRatwAssume : timelyRatwAssumeList) {
            IndicatorAnomalyWarningVo timelyWarningVo = judgeTimely(timelyRatwAssume.getUserId(), timelyRatwAssume.getRealName(), timelyRatwAssume.getIndicatorValue());
            if (timelyWarningVo.getWarningCopyWriting() != null) {
                sendMessage(timelyWarningVo, Collections.singletonList(timelyRatwAssume.getUserId()));
            }
        }
        // 大区服务 - 担当指标首次预约准时上门率
        Map<String, String> firstPunctualRateMap = new HashMap<>(4);
        firstPunctualRateMap.put("businessCode", "district");
        firstPunctualRateMap.put("month", month);
        firstPunctualRateMap.put("indicatorCode", "firstPunctualRate");
        List<AssumeUserDto> firstPunctualRateAssumeList = indicatorWarningDao.getResponsibilityAssume(firstPunctualRateMap);
        for (AssumeUserDto firstPunctualRateAssume : firstPunctualRateAssumeList) {
            IndicatorAnomalyWarningVo firstPunctualityWarningVo = judgePunctuality(firstPunctualRateAssume.getUserId(), firstPunctualRateAssume.getRealName(),
                    firstPunctualRateAssume.getIndicatorValue(),  "首次预约");
            if (firstPunctualityWarningVo.getWarningCopyWriting() != null) {
                sendMessage(firstPunctualityWarningVo, Collections.singletonList(firstPunctualRateAssume.getUserId()));
            }
        }
        // 大区服务 - 担当指标非首次预约准时上门率
        Map<String, String> secondPunctualRateMap = new HashMap<>(4);
        secondPunctualRateMap.put("businessCode", "district");
        secondPunctualRateMap.put("month", month);
        secondPunctualRateMap.put("indicatorCode", "secondPunctualRate");
        List<AssumeUserDto> secondPunctualRateAssumeList = indicatorWarningDao.getResponsibilityAssume(secondPunctualRateMap);
        for (AssumeUserDto secondPunctualRateAssume : secondPunctualRateAssumeList) {
            IndicatorAnomalyWarningVo secondPunctualityWarningVo = judgePunctuality(secondPunctualRateAssume.getUserId(), secondPunctualRateAssume.getRealName(),
                    secondPunctualRateAssume.getIndicatorValue(), "预约");
            if (secondPunctualityWarningVo.getWarningCopyWriting() != null) {
                sendMessage(secondPunctualityWarningVo, Collections.singletonList(secondPunctualRateAssume.getUserId()));
            }
        }
        // 大区服务 - 担当指标TAT平均服务完成时长
        Map<String, String> avgTimeRateMap = new HashMap<>(4);
        avgTimeRateMap.put("businessCode", "district");
        avgTimeRateMap.put("month", month);
        avgTimeRateMap.put("indicatorCode", "avgTimeDay");
        List<AssumeUserDto> avgTimeRateAssumeList = indicatorWarningDao.getResponsibilityAssume(avgTimeRateMap);
        for (AssumeUserDto avgTimeRateAssume : avgTimeRateAssumeList) {
            IndicatorAnomalyWarningVo averageWarningVo = judgeAverage(avgTimeRateAssume.getUserId(), avgTimeRateAssume.getRealName(), avgTimeRateAssume.getIndicatorValue());
            if (averageWarningVo.getWarningCopyWriting() != null) {
                sendMessage(averageWarningVo, Collections.singletonList(avgTimeRateAssume.getUserId()));
            }
        }
        // 大区服务 -  担当指标投诉7天解决率
        Map<String, String> resolutionRateMap = new HashMap<>(4);
        resolutionRateMap.put("businessCode", "district");
        resolutionRateMap.put("month", month);
        resolutionRateMap.put("indicatorCode", "resolutionRate");
        List<AssumeUserDto> resolutionRateAssumeList = indicatorWarningDao.getResponsibilityAssume(resolutionRateMap);
        for (AssumeUserDto resolutionRateAssume : resolutionRateAssumeList) {
            IndicatorAnomalyWarningVo solveWarningVo = judgeSolve(resolutionRateAssume.getUserId(), resolutionRateAssume.getRealName(), resolutionRateAssume.getIndicatorValue());
            if (solveWarningVo.getWarningCopyWriting() != null) {
                sendMessage(solveWarningVo, Collections.singletonList(resolutionRateAssume.getUserId()));
            }
        }
        // 大区服务 - 担当指标一次修复率小于阈值
        Map<String, String> repairRateMap = new HashMap<>(4);
        repairRateMap.put("businessCode", "district");
        repairRateMap.put("month", month);
        repairRateMap.put("indicatorCode", "repairRate");
        List<AssumeUserDto> repairRateAssumeList = indicatorWarningDao.getResponsibilityAssume(repairRateMap);
        for (AssumeUserDto repairRateAssume : repairRateAssumeList) {
            IndicatorAnomalyWarningVo repairWarningVo = judgeRepair(repairRateAssume.getUserId(), repairRateAssume.getRealName(), repairRateAssume.getIndicatorValue());
            if (repairWarningVo.getWarningCopyWriting() != null) {
                sendMessage(repairWarningVo, Collections.singletonList(repairRateAssume.getUserId()));
            }
        }

        // 大区服务 - 担当指标N+1投诉解决方案提交率
        Map<String, String> subRateMap = new HashMap<>(4);
        subRateMap.put("businessCode", "district");
        subRateMap.put("month", month);
        subRateMap.put("indicatorCode", "subRate");
        List<AssumeUserDto> subRateAssumeList = indicatorWarningDao.getResponsibilityAssume(subRateMap);
        for (AssumeUserDto subRateAssume : subRateAssumeList) {
            IndicatorAnomalyWarningVo schemeWarningVo = judgeScheme(subRateAssume.getUserId(), subRateAssume.getRealName(), subRateAssume.getIndicatorValue());
            if (schemeWarningVo.getWarningCopyWriting() != null) {
                sendMessage(schemeWarningVo, Collections.singletonList(subRateAssume.getUserId()));
            }
        }
    }

    /**
     * 投诉直辖
     * @return
     */
    public void complaint() {
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        List<AssumeUserDto> complaintAllAssume = indicatorWarningDao.getComplaintAllAssume();
        List<String> accountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(complaintAllAssume)) {
            accountIdList = complaintAllAssume.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }
        // 投诉直辖 - 判断投诉7天解决率
        IndicatorAnomalyWarningVo sevenDaySolveWarningVo = judgeSevenDaySolveRate();
        if (sevenDaySolveWarningVo.getWarningCopyWriting() != null) {
            sendMessage(sevenDaySolveWarningVo, accountIdList);
        }
        // 投诉直辖 - 投诉6天未结案
        List<Map<String, Object>> sixDayNoOverCaseList = indicatorWarningDao.sixDayNoOverCase();
        for (Map<String, Object> sixDayNoOverCase : sixDayNoOverCaseList) {
            IndicatorAnomalyWarningVo sixDayNoOverWarningVo = new IndicatorAnomalyWarningVo();
            sixDayNoOverWarningVo.setWarningValue("投诉6天未结案");
            String warningCopyWriting = "投诉处理单：" + sixDayNoOverCase.get("complaintNumber").toString() + "已6天未结案";
            sixDayNoOverWarningVo.setWarningCopyWriting(warningCopyWriting);
            sendMessage(sixDayNoOverWarningVo, accountIdList);
        }
        // 投诉直辖 - 投诉大于1天未提交方案
        List<Map<String, Object>> oneDayNoSolutionList = indicatorWarningDao.oneDayNoSolution();
        for (Map<String, Object> onDayNoSolution : oneDayNoSolutionList) {
            IndicatorAnomalyWarningVo oneDayNoSolutionWarningVo = new IndicatorAnomalyWarningVo();
            oneDayNoSolutionWarningVo.setWarningValue("投诉大于1天未提交方案");
            String warningCopyWriting = "投诉处理单：" + onDayNoSolution.get("complaintNumber").toString()
                    + "已" + onDayNoSolution.get("overtimeDay") + "天方案未确认";
            oneDayNoSolutionWarningVo.setWarningCopyWriting(warningCopyWriting);
            sendMessage(oneDayNoSolutionWarningVo, accountIdList);
        }
        // 投诉直辖 - 判断投诉率大于阈值
        IndicatorAnomalyWarningVo judgeComplaintWarningVo = judgeComplaintRate();
        if (judgeComplaintWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeComplaintWarningVo, accountIdList);
        }
    }

    /**
     * 技术品质
     * @return
     */
    public void category() {
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        List<AssumeUserDto> technicalQualityAllAssumeList = indicatorWarningDao.getTechnicalQualityAllAssume();
        List<String> accountIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(technicalQualityAllAssumeList)) {
            accountIdList = technicalQualityAllAssumeList.stream().map(AssumeUserDto::getUserId).collect(Collectors.toList());
        }
        // 技术品质 - 判断一次性修复率
        IndicatorAnomalyWarningVo judgeCalculateRepairWarningVo = judgeCalculateRepairRate(null, null, null);
        if (judgeCalculateRepairWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeCalculateRepairWarningVo, accountIdList);
        }
        // 技术品质 - 判断品质单审核作业时长
        IndicatorAnomalyWarningVo judgeCalculateApprovalWarningVo = judgeCalculateApprovalDuration(null, null);
        if (judgeCalculateApprovalWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeCalculateApprovalWarningVo, accountIdList);
        }
        // 技术品质 - 判断新品上市资料7天内完备率
        IndicatorAnomalyWarningVo judgeCompletionWarningVo = judgeCompletionRate();
        if (judgeCompletionWarningVo.getWarningCopyWriting() != null) {
            sendMessage(judgeCompletionWarningVo, accountIdList);
        }
        // 技术品质 - 担当指标一次性修复率
        Map<String, String> repairRateMap = new HashMap<>(4);
        repairRateMap.put("businessCode", "category");
        repairRateMap.put("month", month);
        repairRateMap.put("indicatorCode", "repairRate");
        List<AssumeUserDto> repairRateAssumeList = indicatorWarningDao.getResponsibilityAssume(repairRateMap);
        for (AssumeUserDto repairRateAssume : repairRateAssumeList) {
            IndicatorAnomalyWarningVo judgeCalculateRepairRateWarningVo = judgeCalculateRepairRate(repairRateAssume.getUserId(), repairRateAssume.getRealName(), repairRateAssume.getIndicatorValue());
            if (judgeCalculateRepairRateWarningVo.getWarningCopyWriting() != null) {
                sendMessage(judgeCalculateRepairRateWarningVo, Collections.singletonList(repairRateAssume.getUserId()));
            }
        }
        // 技术品质 - 担当所负责品类品质审核单时长超过阈值
        Map<String, String> approvalDurationMap = new HashMap<>(4);
        approvalDurationMap.put("businessCode", "category");
        approvalDurationMap.put("month", month);
        approvalDurationMap.put("indicatorCode", "approvalDuration");
        List<AssumeUserDto> approvalDurationAssumeList = indicatorWarningDao.getResponsibilityAssume(approvalDurationMap);
        for (AssumeUserDto approvalDurationAssume : approvalDurationAssumeList) {
            IndicatorAnomalyWarningVo judgeCalculateApprovalDurationWarningVo = judgeCalculateApprovalDuration(approvalDurationAssume.getUserId(), approvalDurationAssume.getIndicatorValue());
            if (judgeCalculateApprovalDurationWarningVo.getWarningCopyWriting() != null) {
                sendMessage(judgeCalculateApprovalDurationWarningVo, Collections.singletonList(approvalDurationAssume.getUserId()));
            }
        }
        // 技术品质 - 新品上市资料达到六天未上传
        Map<String, String> sixDaysDataNotUploadMap = new HashMap<>(4);
        sixDaysDataNotUploadMap.put("businessCode", "category");
        sixDaysDataNotUploadMap.put("month", month);
        List<AssumeUserDto> sixDaysDataNotUploadAssumeList = indicatorWarningDao.getResponsibilityAssume(sixDaysDataNotUploadMap);
        for (AssumeUserDto sixDaysDataNotUploadAssume : sixDaysDataNotUploadAssumeList) {
            IndicatorAnomalyWarningVo sixDaysDataNotUploadWarningVo = new IndicatorAnomalyWarningVo();
            Map<String, Object> map = new HashMap<>(4);
            map.put("startTime", getFirstDayMonth());
            map.put("endTime", getLastDayMonth());
            List<String> productTypeCodeList = indicatorWarningDao.getProductTypeCodeByUserId(sixDaysDataNotUploadAssume.getUserId());
            map.put("productTypeCodeList", productTypeCodeList);
            List<Map<String, Object>> sixDaysDataNotUploadList = indicatorWarningDao.sixDaysDataNotUpload(map);
            sixDaysDataNotUploadWarningVo.setWarningValue("担当所负责品类新品 新品上市资料 达到六天未上传");
            for (Map<String, Object> sixDaysDataNotUpload : sixDaysDataNotUploadList) {
                String warningCopyWriting = "新品上市资料：" + sixDaysDataNotUpload.get("productModel").toString() + "已历时6天未上传";
                sixDaysDataNotUploadWarningVo.setWarningCopyWriting(warningCopyWriting);
                sendMessage(sixDaysDataNotUploadWarningVo, Collections.singletonList(sixDaysDataNotUploadAssume.getUserId()));
            }
        }
    }

    /**
     * 判断部品出货即纳率是否达到月度值
     * @param userId 用户id
     * @param userName 用户名称
     * @param assumeShipment 担当用户设置的目标
     * @return
     */
    public IndicatorAnomalyWarningVo judgeShipment(String userId, String userName, String assumeShipment) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        Double shipment = coreIndexServiceImpl.getShipment(firstDay, lastDay, userId,inventoryDate);
        if (shipment != null) {
            if (shipment > 100) {
                shipment = 100d;
            }
        } else {
            shipment = 0d;
        }
        IndicatorDTO indicatorDTO = indicatorWarningDao.selectTargetByIndicatorCode("partManagement", month, "partImmediate");
        Double targetShipment = indicatorDTO == null ? 0 : Double.parseDouble(indicatorDTO.getIndicatorValue());
        if (userId != null) {
            targetShipment = Double.parseDouble(assumeShipment);
        }
        if (targetShipment != null && targetShipment > shipment) {
            String warningCopyWriting = userId == null ? "核心" : userName;
            if (userId == null) {
                warningVo.setWarningValue("核心指标-部品出货即纳率小于阈值");
            } else {
                warningVo.setWarningValue("担当指标-部品出货即纳率小于阈值");
            }
            String shipmentString = shipment.toString();
            if (shipment % 1 == 0) {
                shipmentString = String.valueOf((int)Math.floor(shipment));
            }
            String targetShipmentString = targetShipment.toString();
            if (targetShipment % 1 == 0) {
                targetShipmentString = String.valueOf((int)Math.floor(targetShipment));
            }
            warningCopyWriting = warningCopyWriting + "指标：部品出货即纳率低于" + month
                    + "目标，当前为" + shipmentString + "%/目标" + targetShipmentString + "%";
            warningVo.setWarningCopyWriting(warningCopyWriting);
        }
        return warningVo;
    }

    /**
     * 判断在库金额是否大于阈值
     * @param userId 用户id
     * @param userName 用户名称
     * @param assumeStockAmount 担当用户的目标
     * @return
     */
    public IndicatorAnomalyWarningVo judgeStockAmount(String userId, String userName, String assumeStockAmount) {
        LocalDate now = LocalDate.now();
        String startDate = now.toString();
        String endDate = now.plusDays(1).toString();
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        double amount = inventoryWarningDao.getAmountByUserId(userId, startDate, endDate).stream().filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).sum();
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("partManagement", month, "stockAmount");
        int targetStockAmount = targetIndicator == null ? 0 : Integer.parseInt(targetIndicator.getIndicatorValue());
        if (userId != null) {
            targetStockAmount = Integer.parseInt(assumeStockAmount);
        }
        if (amount > targetStockAmount) {
            if (userId == null) {
                warningVo.setWarningValue("核心指标-在库金额大于阈值");
            } else {
                warningVo.setWarningValue("担当指标-在库金额大于阈值");
            }
            String warningCopyWriting = userId == null ? "核心" : userName;
            BigDecimal bigDecimal = new BigDecimal(amount);
            warningCopyWriting = warningCopyWriting + "指标：在库金额大于" + month + "目标，当前为"
                    + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "仟元/目标" + targetStockAmount + "仟元";
            warningVo.setWarningCopyWriting(warningCopyWriting);
        }
        return warningVo;
    }

    /**
     * 判断NDS2是否低于阈值
     * @param userId 担当用户id
     * @param userName 担当用户姓名
     * @param assumeNds2 担当用户的值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeNds2(String userId, String userName, String assumeNds2) {
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String firstDay = getFirstDayMonth().substring(0, 10);
        String lastDay = getLastDayMonth().substring(0, 10);
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        // 获取NDS2
        int countByDate = receiptDao.getCountByDate(firstDay, lastDay, userId, inventoryDate);
        int countByNds2 = receiptDao.getCountByNDS2(firstDay, lastDay, userId, inventoryDate);
        Double nds2 = TimeUtil.getRate(countByNds2,countByDate);
        if (nds2 == null) {
            nds2 = 0d;
        }
        // 获取本月的nds2目标
        IndicatorDTO nowNds2 = indicatorWarningDao.selectTargetByIndicatorCode("partManagement",month,"nds2");
        Double targetNds2 = nowNds2 == null ? 0 : Double.parseDouble(nowNds2.getIndicatorValue());
        if (userId != null) {
            targetNds2 = Double.parseDouble(assumeNds2);
        }
        if (nds2 < targetNds2) {
            if (userId == null) {
                warningVo.setWarningValue("核心指标-NDS2低于阈值");
            } else {
                warningVo.setWarningValue("担当指标-NDS2低于阈值");
            }
            String warningCopyWriting = userId == null ? "核心" : userName;
            String nds2String = nds2.toString();
            if (nds2 % 1 == 0) {
                nds2String = String.valueOf((int)Math.floor(nds2));
            }
            String targetNds2String = targetNds2.toString();
            if (targetNds2 % 1 == 0) {
                targetNds2String = String.valueOf((int)Math.floor(targetNds2));
            }
            warningCopyWriting = warningCopyWriting + "指标：NDS2低于" + month + "目标，当前为" + nds2String + "%/目标" + targetNds2String + "%";
            warningVo.setWarningCopyWriting(warningCopyWriting);
        }
        return warningVo;
    }

    /**
     * 判断30分钟及时预约率
     * @param userId 用户id
     * @param userName 用户姓名
     * @param assumeRate 担当用户设置的目标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeTimely(String userId, String userName, String assumeRate) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        Map<String, String> map = new HashMap<>(4);
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        if (userId != null) {
            map.put("userId", userId);
        }
        Map<String, Object> timely = indicatorWarningDao.timely(map);
        Double rate = Double.parseDouble(timely.get("rate").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "timelyRatw");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-30分钟及时预约率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-30分钟及时预约率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：30分钟及时预约率低于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断首次/二次预约准时上门率
     * @param userId 用户id
     * @param userName 用户姓名
     * @param assumeRate 担当用户设置的指标值
     * @param type 预约类型：首次预约，预约
     * @return
     */
    public IndicatorAnomalyWarningVo judgePunctuality(String userId, String userName, String assumeRate, String type) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        Map<String, String> map = new HashMap<>(4);
        if (userId != null) {
            map.put("userId", userId);
        }
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        Map<String, Object> firstPunctuality = "首次预约".equals(type) ? indicatorWarningDao.firstPunctuality(map) : indicatorWarningDao.punctuality(map);
        Double rate = Double.parseDouble(firstPunctuality.get("rate").toString());
        IndicatorDTO targetIndicator;
        if ("首次预约".equals(type)) {
            targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "firstPunctualRate");
        } else {
            targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "secondPunctualRate");
        }
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-" + type + "准时上门率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-" + type + "准时上门率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：" + type + "准时上门率低于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断TAT平均服务完成时长
     * @param userId 用户id
     * @param userName 用户姓名
     * @param assumeAverageHour 责任担当设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeAverage(String userId, String userName, String assumeAverageHour) {

        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        Map<String, String> map = new HashMap<>(4);
        map.put("startTime", firstDay);
        if (userId != null) {
            map.put("userId", userId);
        }
        map.put("endTime", lastDay);
        Map<String, Object> average = indicatorWarningDao.average(map);
        Double averageHour  = average == null ? 0 : Double.parseDouble(average.get("ave").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "avgTimeDay");
        if (targetIndicator != null) {
            Double targetHour = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetHour = Double.parseDouble(assumeAverageHour);
            }
            if (averageHour > targetHour) {
                String averageDay = ((int) Math.floor(averageHour/24)) != 0 ? ((int) Math.floor(averageHour/24)) + "天" : "";
                if (((int) (averageHour%24)) > 0) {
                    averageDay = averageDay + ((int) (averageHour % 24)) + "小时";
                }
                String targetDay = ((int) Math.floor(targetHour/24)) != 0 ? ((int) Math.floor(targetHour/24)) + "天" : "";
                if (((int) (targetHour%24)) > 0) {
                    targetDay = targetDay + ((int) (targetHour % 24)) + "小时";
                }
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-TAT平均服务完成时长大于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-TAT平均服务完成市场大于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                warningCopyWriting = warningCopyWriting + "指标：TAT平均服务完成时长大于" + month + "目标，当前为" + averageDay + "/目标" + targetDay;
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断投诉七天解决率
     * @param userId 用户id
     * @param userName 用户名称
     * @param assumeRate 责任担当设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeSolve(String userId, String userName, String assumeRate) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        Map<String, String> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        map.put("startTime", firstDay);
        if (userId != null) {
            map.put("userId", userId);
        }
        map.put("endTime", lastDay);
        Map<String, Object> solve = indicatorWarningDao.solve(map);
        Double rate = Double.parseDouble(solve.get("rate").toString());
        int total = Integer.parseInt(solve.get("total").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "resolutionRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate && total > 0) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-投诉7天解决率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-投诉7天解决率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：投诉7天解决率低于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断一次修复率
     * @param userId 用户id
     * @param userName 用户名称
     * @param assumeRate 责任担当设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeRepair(String userId, String userName, String assumeRate) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        Map<String, String> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        map.put("startTime", firstDay);
        if (userId != null) {
            map.put("userId", userId);
        }
        map.put("endTime", lastDay);
        Map<String, Object> repair = indicatorWarningDao.repair(map);
        Double rate = Double.parseDouble(repair.get("rate").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "repairRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-一次修复率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-一次修复率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：一次修复率低于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断N+1投诉解决方案提交率
     * @param userId 用户id
     * @param userName 用户名称
     * @param assumeRate 责任担当设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeScheme(String userId, String userName, String assumeRate) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        Map<String, String> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        if (userId != null) {
            map.put("userId", userId);
        }
        Map<String, Object> scheme = indicatorWarningDao.scheme(map);
        Double rate = Double.parseDouble(scheme.get("rate").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("district", month, "subRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-N+1投诉解决方案解决率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-N+1投诉解决方案解决率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：N+1投诉解决方案低于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 投诉直辖-投诉7天解决率小于阈值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeSevenDaySolveRate() {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        ComplaintDto complaintDto = new ComplaintDto();
        complaintDto.setBeginDate(getFirstDayMonth());
        complaintDto.setEndDate(getLastDayMonth());
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        //查询投诉7天解决率和投诉件数
        Map<String,Object> sevenDaySolve = complaintsDao.sevenDaySolveRate(complaintDto);
        Double sevenSolveRate = Double.parseDouble(sevenDaySolve.get("sevenSolveRate").toString().replace("%", ""));
        //获取当月目标
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("complaint", month, "complaintRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (sevenSolveRate < targetRate) {
                warningVo.setWarningValue("投诉7天解决率小于阈值");
                String sevenSolveRateString = sevenSolveRate.toString();
                if (sevenSolveRate % 1 == 0) {
                    sevenSolveRateString = String.valueOf((int)Math.floor(sevenSolveRate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                String warningCopyWriting = "核心指标：投诉7天解决率低于" + month + "目标，当前为" + sevenSolveRateString  + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 判断投诉率大于阈值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeComplaintRate() {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        ComplaintDto complaintDto = new ComplaintDto();
        complaintDto.setBeginDate(getFirstDayMonth());
        complaintDto.setEndDate(getLastDayMonth());
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        //查询投诉率
        Map<String,Object> complaintRate = complaintsDao.complaintRate(complaintDto);
        Double rate = Double.parseDouble(complaintRate.get("complaintRate").toString().replace("%", ""));
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("complaint", month, "warningRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (rate > targetRate) {
                warningVo.setWarningValue("投诉率大于阈值");
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                String warningCopyWriting = "核心指标：投诉率大于" + month + "目标，当前为" + rateString  + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 技术品质 - 判断一次性修复率小于阈值
     * @param userId 用户id
     * @param userName 用户姓名
     * @param assumeRate 担当指标设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeCalculateRepairRate(String userId, String userName, String assumeRate) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        Map<String, Object> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        List<String> productTypeCodeList = indicatorWarningDao.getProductTypeCodeByUserId(userId);
        map.put("productTypeCodeList", productTypeCodeList);
        Map<String, Object> repair = indicatorWarningDao.calculateRepairRate(map);
        Double rate = Double.parseDouble(repair.get("repair").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("category", month, "repairRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (userId != null) {
                targetRate = Double.parseDouble(assumeRate);
            }
            if (rate < targetRate) {
                if (userId == null) {
                    warningVo.setWarningValue("核心指标-一次修复率小于阈值");
                } else {
                    warningVo.setWarningValue("担当指标-一次修复率小于阈值");
                }
                String warningCopyWriting = userId == null ? "核心" : userName;
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                warningCopyWriting = warningCopyWriting + "指标：一次修复率小于" + month + "目标，当前为" +
                        rateString  + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 技术品质 - 判断审核作业时长
     * @param userId 用户id
     * @param assumeTime 担当用户设置的指标值
     * @return
     */
    public IndicatorAnomalyWarningVo judgeCalculateApprovalDuration(String userId, String assumeTime) {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        Map<String, Object> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        List<String> productTypeCodeList = indicatorWarningDao.getProductTypeCodeByUserId(userId);
        map.put("productTypeCodeList", productTypeCodeList);
        List<Map<String, Object>> calculateApprovalDurationList = new ArrayList<>();
        if (userId == null) {
            calculateApprovalDurationList.add(indicatorWarningDao.calculateApprovalDuration(map));
        } else {
            calculateApprovalDurationList = indicatorWarningDao.calculateAssumeApprovalDuration(map);
        }
        for (Map<String, Object> calculateApprovalDuration : calculateApprovalDurationList) {
            Double time = calculateApprovalDuration == null ? 0d : Double.parseDouble(calculateApprovalDuration.get("time").toString());
            IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("category", month, "approvalDuration");
            if (targetIndicator != null && calculateApprovalDuration != null) {
                Double targetTime = Double.parseDouble(targetIndicator.getIndicatorValue());
                if (userId != null) {
                    targetTime = Double.parseDouble(assumeTime);
                }
                if (time > targetTime) {
                    if (userId == null) {
                        warningVo.setWarningValue("核心指标-品质单审核作业时长超过阈值");
                        String warningCopyWriting = "核心指标：品质单审核作业时长大于" + month + "目标，当前为" + time + "/目标" + targetTime;
                        warningVo.setWarningCopyWriting(warningCopyWriting);
                    } else {
                        warningVo.setWarningValue("担当所负责品类品质审核单时长超过阈值");
                        String warningCopyWriting = "品质审核单：" + calculateApprovalDuration.get("manageNumber") + "已历时" + time;
                        warningVo.setWarningCopyWriting(warningCopyWriting);
                    }
                }
            }
        }
        return warningVo;
    }

    /**
     * 判断七天内完备率
     * @return
     */
    public IndicatorAnomalyWarningVo judgeCompletionRate() {
        IndicatorAnomalyWarningVo warningVo = new IndicatorAnomalyWarningVo();
        Map<String, Object> map = new HashMap<>(4);
        String firstDay = getFirstDayMonth();
        String lastDay = getLastDayMonth();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        map.put("startTime", firstDay);
        map.put("endTime", lastDay);
        List<String> productTypeCodeList = indicatorWarningDao.getProductTypeCodeByUserId(null);
        map.put("productTypeCodeList", productTypeCodeList);
        Map<String, Object> completionRate = indicatorWarningDao.calculateCompletionRate(map);
        Double rate = Double.parseDouble(completionRate.get("rate").toString());
        IndicatorDTO targetIndicator = indicatorWarningDao.selectTargetByIndicatorCode("category", month, "completionRate");
        if (targetIndicator != null) {
            Double targetRate = Double.parseDouble(targetIndicator.getIndicatorValue());
            if (rate < targetRate) {
                warningVo.setWarningValue("核心指标-新品上市资料7天内完备率");
                String rateString = rate.toString();
                if (rate % 1 == 0) {
                    rateString = String.valueOf((int)Math.floor(rate));
                }
                String targetRateString = targetRate.toString();
                if (targetRate % 1 == 0) {
                    targetRateString = String.valueOf((int)Math.floor(targetRate));
                }
                String warningCopyWriting = "核心指标：新品上市资料7天内完备率小于" + month + "目标，当前为" + rateString + "%/目标" + targetRateString + "%";
                warningVo.setWarningCopyWriting(warningCopyWriting);
            }
        }
        return warningVo;
    }

    /**
     * 发送信息
     * @param warningVo 发送通知的信息
     * @param accountIdList 通知的用户的账户id
     */
    public void sendMessage(IndicatorAnomalyWarningVo warningVo, List<String> accountIdList) {
        if (accountIdList != null && accountIdList.size() > 0) {
            List<UserInfo> userInfos = indicatorWarningDao.getUserInfoByAccountId(accountIdList);
            if (CollectionUtils.isNotEmpty(userInfos)) {
                MessageUtil.send("预警", warningVo.getWarningCopyWriting(), "auto", userInfos);
            }
        }
    }

    /**
     * 获取当前月份的第一天
     * @return
     */
    public String getFirstDayMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.set(Calendar.DAY_OF_MONTH,1);
        instance.set(Calendar.HOUR_OF_DAY,0);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        // 当前月份的第一天
        return format.format(instance.getTime());
    }

    /**
     * 获取当前月份的最后一天
     * @return
     */
    public String getLastDayMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.MONTH, 1);
        instance.set(Calendar.DAY_OF_MONTH,1);
        instance.set(Calendar.HOUR_OF_DAY,23);
        instance.set(Calendar.MINUTE,59);
        instance.set(Calendar.SECOND,59);
        instance.add(Calendar.DAY_OF_MONTH, -1);
        // 当前月份的最后一天
        return format.format(instance.getTime());
    }
}
