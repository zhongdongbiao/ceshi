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
public interface TechnicalQualityCopyDao {
    /**
     * 工程师管理-带筛选
     */
    @DS("git_adb")
    List<EngineerCopyDTO> selectEngineer(EngineerQueryDTO engineerQueryDTO);
    /**
     * 工程师管理-带筛选
     */
    @DS("git_adb")
    List<String> selectEngineers(EngineerQueryDTO engineerQueryDTO);
    /**
     * 全年月一次性修复率
     */
    List<ExportRepairRateByMonthDTO> exportRepairRateByMonth(ExportConditionDTO exportConditionDTO);
    /**
     * 新品上市资料七天内完备率页面查询
     */
    @DS("git_adb")
    List<CompletionRateDTO> selectCompletionRate(CompletionRateQueryDTO completionRateQueryDTO);
    /**
     * 通过品类新品上市资料七天内完备率页面
     */
    @DS("git_adb")
    List<CompletionRateByCategoryDTO> selectCompletionRateByCategory(CompletionRateByCategoryQueryDTO completionRateByCategoryQueryDTO);
    /**
     * 品质单审核作业时长页面列表
     */
    @DS("git_adb")
    List<ApprovalDurationDTO> selectApprovalDuration(ApprovalDurationQueryDTO approvalDurationQueryDTO);
    /**
     * 查询服务单列表
     */
    @DS("git_adb")
    List<RepairRateDTO> selectServiceList(RepairRateQueryDTO repairRateQueryDTO);
}
