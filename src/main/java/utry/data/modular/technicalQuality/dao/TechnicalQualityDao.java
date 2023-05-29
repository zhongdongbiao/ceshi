package utry.data.modular.technicalQuality.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.dto.QualityFeedbackDTO;
import utry.data.modular.technicalQuality.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 品质反馈单详情
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface TechnicalQualityDao {
    /**
     * 查询所有品类-类型-型号信息
     */
    @DS("git_adb")
    List<TreeDTO> selectCategoryInformation();
    /**
     * 查询所有品类
     */
    @DS("git_adb")
    List<TreeDTO> selectRoot();
    /**
     * 筛选配置过目标的担当
     */
    @DS("git_adb")
    List<TechnicalQualityUserDTO> selectUserByConfig(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 查询符合类型条件的关联担当
     */
    @DS("git_adb")
    List<TechnicalQualityUserDTO> selectUserByType(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 担当的本月指标值
     */
    @DS("git_adb")
    List<IndicatorUserDTO> selectMonthIndicator(@Param("accountId") String accountId,@Param("startTime") String startTime);
    /**
     * 一次性修复率查询
     */
    @DS("git_adb")
    Map<String,Object> calculateRepairRate(CalculateDTO calculateDTO);
    /**
     * 查询担当所配置的类型
     */
    @DS("git_adb")
    List<String> selectUserType(String accountId);
    /**
     * 品质单审核作业时长查询
     */
    @DS("git_adb")
    Map<String,Object> calculateApprovalDuration(CalculateDTO calculateDTO);
    /**
     * 新品上市资料七天完备率查询
     */
    @DS("git_adb")
    Map<String, Object> calculateCompletionRate(CalculateDTO calculateDTO);
    /**
     * 查询核心目标值
     */
    @DS("git_adb")
    List<IndicatorUserDTO> selectTargetMonthIndicator(String startTime);
    /**
     * 工程师管理-带筛选
     */
    @DS("git_adb")
    List<EngineerDTO> selectEngineer(EngineerQueryDTO engineerQueryDTO);
    /**
     * 一次性修复率/类型别
     */
    @DS("git_adb")
    List<RepairRateHistogramDTO> selectRepairRateByType(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 一次性修复率/大区别
     */
    @DS("git_adb")
    List<RepairRateHistogramDTO> selectRepairRateByAccounting(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 一次性修复率-工程师品类
     */
    @DS("git_adb")
    List<EngineerDTO> selectEngineerCategory(EngineerDTO engineerDTO);
    /**
     * 一次性修复率-工程师类型
     */
    @DS("git_adb")
    List<EngineerDTO> selectEngineerType(EngineerDTO engineerDTOO);
    /**
     * 产品资料旧数据查询
     */
    List<QualityFeedbackDTO> queryOldInformation();
    /**
     * 品质单审核作业时长页面列表
     */
    @DS("git_adb")
    List<ApprovalDurationDTO> selectApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 品质单审核作业时长页面时间轴
     */
    @DS("git_adb")
    ApprovalDurationTimeDTO selectApprovalDurationTime(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 新品上市资料七天内完备率页面查询
     */
    @DS("git_adb")
    List<CompletionRateDTO> selectCompletionRate(CompletionRateQueryDTO completionRateQueryDTO);
    /**
     * 一次性修复率时间轴
     */
    @DS("git_adb")
    List<Map<String,Object>> selectRepairRateTime(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 通过品类新品上市资料七天内完备率页面
     */
    @DS("git_adb")
    List<CompletionRateByCategoryDTO> selectCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO);
    /**
     * 查询服务单列表
     */
    @DS("git_adb")
    List<RepairRateDTO> selectServiceList(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 一次性修复率折线图
     */
    List<LineChartDTO> selectRepairRateLineChart(LineChartQueryDTO lineChartQueryDTO);
    /**
     * 故障分析列表---未筛选
     */
    @DS("git_adb")
    List<PieChartDTO> selectRepairRatePieChart(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 故障分析饼图
     */
    @DS("git_adb")
    List<FaultCauseDTO> selectFaultCause(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 故障分析列表---筛选
     */
    @DS("git_adb")
    List<PieChartDTO> selectPartByFaultCause(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 查询最新月份一次性修复率
     */
    @DS("git_adb")
    String selectThreshold();
    /**
     * 全年一次性修复率
     */
    List<ExportRepairRateDTO> exportRepairRateByYear(ExportConditionDTO exportConditionDTO);
    /**
     * 全年月一次性修复率
     */
    List<ExportRepairRateByMonthDTO> exportRepairRateByMonth(ExportConditionDTO exportConditionDTO);
    /**
     * 导出---服务单列表
     */
    @DS("git_adb")
    List<RepairRateDTO> exportServiceList(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 获取挂单解挂时间
     */
    @DS("git_adb")
    List<Map<String, Object>> selectPendingOrder(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 品质单审核作业平均时长查询
     */
    @DS("git_adb")
    String getApprovalDurationAvgTime(CalculateDTO calculateDTO);
    /***
     *  查询所有人员类型
     */
    @DS("git_adb")
    List<String> selectAllUserType();
    /***
     *  查询人员本月目标
     */
    @DS("git_adb")
    List<IndicatorUserDTO> selectUserMonthIndicator(@Param("accountId") String accountId,@Param("operationTime") String operationTime);
    /***
     *  查询详情时间轴
     */
    @DS("git_adb")
    ApprovalDurationTimeDTO selectDetailApprovalDurationTime(DetailTimeDTO detailTimeDTO);
    /***
     *  查询模板品类
     */
    List<DistrictTemplateQueryDTO> selectCategoryTemplate();
    /***
     *  查询模板类型
     */
    List<DistrictTemplateQueryDTO> selectTypeTemplate();
}
