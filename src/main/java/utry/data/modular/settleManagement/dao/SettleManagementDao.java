package utry.data.modular.settleManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.settleManagement.dto.ConditionDto;
import utry.data.modular.settleManagement.dto.SettleDataDto;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 11:29
 */
@Mapper
public interface SettleManagementDao {

    /**
     * 将新增的数据插入到已结表
     * @param insertSettleDataList
     */
    void insertSettleData(@Param("list") List<SettleDataDto> insertSettleDataList);

    /**
     * 修改结算表中的数据
     * @param updateSettleDataList
     */
    void updateSettleData(@Param("list") List<SettleDataDto> updateSettleDataList);

    /**
     * 删除结算表中的数据
     * @param deleteSettleDataList
     */
    void deleteSettleData(@Param("list") List<SettleDataDto> deleteSettleDataList);

    /**
     * 查询结算汇总费用数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectSettleSummary(@Param("dto") ConditionDto conditionDto);

    /**
     * 按查询已结算和未结算的服务类型数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectAllServiceType(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出已结算的服务类型数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectServiceType(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询结算和已结算的服务数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectAllCostAnalysis(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询已结算费用分析数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectCostAnalysis(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出已结算和未结算的工厂/营业费用分析数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectAllIndustrialBusiness(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询已结算的工厂/营业费用分析数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectIndustrialBusiness(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询已结算和未结算的工厂别服务违约数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectAllFactoryServiceBreach(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询已结算的工厂别服务违约数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectFactoryServiceBreach(@Param("dto") ConditionDto conditionDto);

    /**
     * 获取所有的结算对象
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> getSettleObjects();

    /**
     * 查询结算单流程监控
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectStatementMonitor(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询服务一个月的数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectServiceTypeMonth(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询服务一年的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectServiceTypeYear(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出费用分析一个月的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectCostAnalysisMonth(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出费用分析一年的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectCostAnalysisYear(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出工厂/营业费用分析一个月的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectIndustrialBusinessMonth(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出工厂/营业费用分析一年的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectIndustrialBusinessYear(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出工厂别服务违约一个月的数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectFactoryServiceMonth(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出工厂别服务违约一年的数据
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectFactoryServiceYear(@Param("dto") ConditionDto conditionDto);
}
