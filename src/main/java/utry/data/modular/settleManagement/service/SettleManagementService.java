package utry.data.modular.settleManagement.service;

import utry.data.modular.settleManagement.dto.ConditionDto;
import utry.data.modular.settleManagement.model.SettleDataVo;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 15:49
 */
public interface SettleManagementService {

    /**
     * 根据条件查询结算服务单汇总信息
     * @param conditionDto
     * @return
     */
    List<Map<String,Object>> selectSettleSummary(ConditionDto conditionDto);

    /**
     * 根据条件查询服务类型-费用分析
     * @param conditionDto
     * @return
     */
    Object selectServiceType(ConditionDto conditionDto,Integer pageNum,Integer pageSize);

    /**
     * 根据条件查询费用分析
     * @param conditionDto
     * @return
     */
    Object selectCostAnalysis(ConditionDto conditionDto,Integer pageNum,Integer pageSize);

    /**
     * 工业/营业费用分析
     * @param conditionDto
     * @return
     */
    Object selectIndustrialBusiness(ConditionDto conditionDto,Integer pageNum,Integer pageSize);

    /**
     * 工厂别服务违约
     * @param conditionDto
     * @return
     */
    Object selectFactoryServiceBreach(ConditionDto conditionDto,Integer pageNum,Integer pageSize);

    /**
     * 结算单流程监控
     * @param conditionDto
     * @return
     */
    List<Map<String, Object>> selectStatementMonitor(ConditionDto conditionDto);

    /**
     * 获取所有的结算对象
     * @return
     */
    List<Map<String, Object>> getSettleObjects();

    /**
     * 获取日期范围
     * @param conditionDto
     * @return
     */
    List<String> getDateRangeList(ConditionDto conditionDto);

    /**
     * 服务类型-费用分析导出
     * @param response
     * @param conditionDto
     */
    void serviceTypeExport(HttpServletResponse response, ConditionDto conditionDto);

    /**
     * 费用分析导出
     * @param response
     * @param conditionDto
     */
    void costAnalysisExport(HttpServletResponse response, ConditionDto conditionDto);

    /**
     * 工厂/营业费用分析导出
     * @param response
     * @param conditionDto
     */
    void industrialBusinessExport(HttpServletResponse response, ConditionDto conditionDto);

    /**
     * 工厂别服务违约导出
     * @param response
     * @param conditionDto
     */
    void factoryServiceBreachExport(HttpServletResponse response, ConditionDto conditionDto);
}
