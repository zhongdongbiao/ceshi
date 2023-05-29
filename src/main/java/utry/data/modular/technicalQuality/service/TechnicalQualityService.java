package utry.data.modular.technicalQuality.service;

import com.alibaba.fastjson.JSONObject;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.dto.QualityFeedbackDTO;
import utry.data.modular.technicalQuality.dto.*;

import java.util.List;
import java.util.Map;

public interface TechnicalQualityService {

    /**
     * 保存筛选条件
     */
    void saveOption(JSONObject jsonObject);
    /**
     * 查询全部选项
     */
    List<JSONObject> selectOptions(JSONObject jsonObject);
    /**
     * 设置首选项
     */
    void setFirstOption(List<JSONObject> jsonObjectList);
    /**
     * 查询全部品类-类型-型号树
     */
    List<TreeDTO> selectCategoryTree();
    /**
     * 查询符合类型条件的担当详情
     */
    List<TechnicalQualityUserDTO> selectUserInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 查询符合类型条件的核心详情
     */
    List<TechnicalQualityUserDTO> selectTargetInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 查询符合类型条件的核心详情
     */
    List<TechnicalQualityUserDTO> selectTargetInfoCopy(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 工程师管理-带筛选
     */
    List<EngineerDTO> selectEngineer(EngineerQueryDTO engineerQueryDTO);
    /**
     * 一次性修复率/类型别/大区别
     */
    List<RepairRateHistogramDTO> selectRepairRate(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 一次性修复率-工程师品类
     */
    List<EngineerDTO> selectEngineerCategory(EngineerDTO engineerDTO);
    /**
     * 一次性修复率-工程师类型
     */
    List<EngineerDTO> selectEngineerType(EngineerDTO engineerDTO);
    /**
     * 产品资料旧数据查询
     */
    List<QualityFeedbackDTO> queryOldInformation();
    /**
     * 产品资料旧数据更新
     */
    void updateInformation(List<QualityFeedbackDTO> olds);
    /**
     * 删除选项
     */
    void deleteOption(List<JSONObject> jsonObjectList);
    /**
     * 品质单审核作业时长页面列表
     */
    List<ApprovalDurationDTO> selectApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 导出品质单审核作业时长页面列表
     */
    List<ApprovalDurationDTO> exportApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 品质单审核作业时长页面时间轴
     */
    ApprovalDurationTimeDTO selectApprovalDurationTime(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 新品上市资料七天内完备率页面
     */
    List<CompletionRateDTO> selectCompletionRate(CompletionRateQueryDTO completionRateQueryDTO);
    /**
     * 导出新品上市资料七天内完备率页面
     */
    List<CompletionRateDTO> exportCompletionRate(CompletionRateQueryDTO completionRateQueryDTO);
    /**
     * 一次性修复率时间轴
     */
    RepairRateTimeDTO selectRepairRateTime(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 通过品类新品上市资料七天内完备率页面
     */
    List<CompletionRateByCategoryDTO> selectCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO);
    /**
     * 通过品类新品上市资料七天内完备率页面
     */
    List<CompletionRateByCategoryDTO> exportCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO);
    /**
     * 服务单列表
     */
    List<RepairRateDTO> selectServiceList(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 一次性修复率折线图
     */
    Map<String,List<LineChartDTO>> selectRepairRateLineChart(LineChartQueryDTO lineChartQueryDTO);
    /**
     * 故障分析饼图---未筛选
     */
    List<PieChartDTO> selectRepairRatePieChart(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 故障分析饼图---筛选
     */
    List<PieChartDTO> selectPartByFaultCause(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 查询最新月份一次性修复率
     */
    String selectThreshold();
    /**
     * 导出---工程师管理
     */
    List<EngineerExportDTO> exportEngineerList(EngineerQueryDTO engineerQueryDTO);
    /**
     * 导出---全年一次性修复率
     */
    List<ExportRepairRateDTO> exportRepairRateByYear(ExportConditionDTO exportConditionDTO);
    /**
     * 导出---服务单列表
     */
    List<RepairRateDTO> exportServiceList(RepairRateQueryDTO repairRateQueryDTO);
    /**
     * 获取平均时长
     */
    String getApprovalDurationAvgTime(CalculateDTO calculateDTO);
    /**
     * 查询人员类型
     */
    List<String> selectType(String accountId);
    /**
     * 查询故障分析饼图
     */
    List<FaultCauseDTO> selectPie(PieChartQueryDTO pieChartQueryDTO);
    /**
     * 获取本月目标
     */
    List<IndicatorUserDTO> selectThisMonth(UserTypeQueryDTO userTypeQueryDTO);
    /**
     * 获取时间轴
     */
    ApprovalDurationTimeDTO selectDetailApprovalDurationTime(DetailTimeDTO detailTimeDTO);
    /**
     * 获取担当
     */
    TechnicalQualityQueryDTO getUsers(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 获取担当详情
     */
    TechnicalQualityUserDTO getUserInfo(TechnicalQualityQueryDTO technicalQualityQueryDTO);
    /**
     * 模板显示
     */
    List<DistrictTemplateDTO> selectDistrictTemplate();
}
