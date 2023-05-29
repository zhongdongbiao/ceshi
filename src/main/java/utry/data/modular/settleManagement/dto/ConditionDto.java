package utry.data.modular.settleManagement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 15:23
 */
@Data
public class ConditionDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "polymerizeWay", value = "聚合方式 1:按月聚合 2:按日聚合")
    private String polymerizeWay;

    @ApiModelProperty(name = "dateRange", value = "日期范围")
    private String dateRange;

    @ApiModelProperty(name = "beginDate", value = "开始日期")
    private String beginDate;

    @ApiModelProperty(name = "endDate", value = "结束日期")
    private String endDate;

    @ApiModelProperty(name = "productTypeCodeList", value = "产品类型集合")
    private List<String> productTypeCodeList;

    @ApiModelProperty(name = "showDimension", value = "展示维度 1:费用维度 2:台量维度")
    private String showDimension;

    @ApiModelProperty(name = "classifyDimension", value = "分类维度 1:区管维度 2:省份维度")
    private String classifyDimension;

    @ApiModelProperty(name = "selectYear", value = "选择年份")
    private String selectYear;

    @ApiModelProperty(name = "selectMonth", value = "选择月份")
    private String selectMonth;

    @ApiModelProperty(name = "selectDay", value = "选择天数")
    private String selectDay;

    @ApiModelProperty(name = "settleObject", value = "结算对象")
    private String settleObject;

    @ApiModelProperty(name = "chartFlag", value = "图表标志 1:图表 2:列表")
    private String chartFlag;

    @ApiModelProperty(name = "orderQuery", value = "排序条件")
    private String orderQuery;

    @ApiModelProperty(name = "screenList", value = "筛选List")
    private Map<String,Object> screenList;

    @ApiModelProperty(name = "screenQuery", value = "筛选条件")
    private List<String> screenQuery;

    @ApiModelProperty(name = "screenRule", value = "筛选规则 在此之间:between 早于:before 晚于:after 当日:day")
    private String screenRule;

    @ApiModelProperty(name = "exportNumber", value = "导出条数")
    private int exportNumber;

}
