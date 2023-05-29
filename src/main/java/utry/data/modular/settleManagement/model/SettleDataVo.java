package utry.data.modular.settleManagement.model;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.math3.analysis.function.Cos;

import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 14:31
 */
@Data
public class SettleDataVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "settleSummaryList", value = "结算服务单汇总")
    private List<CostSettle> settleSummaryList;

    @ApiModelProperty(name = "serviceCostAnalysisList", value = "服务类型费用分析")
    private PageInfo<CostSettle> serviceCostAnalysisList;

    @ApiModelProperty(name = "costAnalysisList", value = "费用分析")
    private PageInfo<CostSettle> costAnalysisList;

    @ApiModelProperty(name = "factoryCostAnalysisList", value = "工厂/营业费用分析")
    private PageInfo<CostSettle> factoryCostAnalysisList;

    @ApiModelProperty(name = "factoryServiceDefault", value = "工厂别服务违约")
    private PageInfo<CostSettle> factoryServiceDefault;

}
