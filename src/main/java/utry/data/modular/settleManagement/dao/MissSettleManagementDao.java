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
 * @Date 2022/4/27 10:18
 */
@Mapper
public interface MissSettleManagementDao {

    /**
     * 将新增的数据插入到未结表
     * @param insertMissSettleDataList
     */
    void insertMissSettleData(@Param("list") List<SettleDataDto> insertMissSettleDataList);

    /**
     * 更新未结算表中的数据
     * @param updateMissSettleDataList
     */
    void updateMissSettleData(@Param("list") List<SettleDataDto> updateMissSettleDataList);

    /**
     * 更新待结算的状态
     * @param dataList
     */
    void updateMissSettleState(@Param("list") List<SettleDataDto> dataList);

    /**
     * 删除未结算表中的数据
     * @param deleteMissSettleDataList
     */
    void deleteMissSettleData(@Param("list") List<SettleDataDto> deleteMissSettleDataList);

    /**
     * 查询未结算汇总数据
     * @param conditionDto
     */
    List<Map<String,Object>> selectMissSettleSummary(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询未结算的服务数据
     * @param conditionDto
     */
    List<Map<String, Object>> selectMissServiceType(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询未结算的费用分析
     * @param conditionDto
     * @return
     */
    @DS("git_adb")
    List<Map<String, Object>> selectMissCostAnalysis(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出未结算的工厂/营业费用分析数据
     * @param conditionDto
     * @return
     */

    List<Map<String, Object>> selectMissIndustrialBusiness(@Param("dto") ConditionDto conditionDto);

    /**
     * 查询出未结算的工厂别服务违约数据
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectMissFactoryServiceBreach(@Param("dto") ConditionDto conditionDto);
}
