package utry.data.modular.complaints.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;
import utry.data.util.DateConditionUtil;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zh
 * @Date 2022/5/11 10:06
 */
@Data
public class ComplaintDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "dateRange", value = "日期范围")
    private String dateRange;

    @ApiModelProperty(name = "beginDate", value = "开始日期")
    private String beginDate;

    @ApiModelProperty(name = "endDate", value = "结束日期")
    private String endDate;

    @ApiModelProperty(name = "productTypeCodeList", value = "产品类型代码集合")
    private List<String> productTypeCodeList;

    @ApiModelProperty(name = "complaintFlag",value = "投诉标志  1:投诉量   2:投诉7天解决率")
    private String complaintFlag;

    @ApiModelProperty(name = "rankFlag", value = "排名标志  1:省份排名  2:大区排名")
    private String rankFlag;

    @ApiModelProperty(name = "orderQuery", value = "排序条件")
    private String orderQuery;

    @ApiModelProperty(name = "screenList", value = "筛选List")
    private Map<String,Object> screenList;

    @ApiModelProperty(name = "screenQuery", value = "筛选条件")
    private List<String> screenQuery;

    @ApiModelProperty(name = "complaintClassify", value = "投诉种类")
    private String complaintClassify;

    @ApiModelProperty(name = "exceptionFlag", value = "仅展示异常单 1:是 0:否")
    private String exceptionFlag;

    @ApiModelProperty(name = "areaCodeList", value = "片区集合")
    private List<String> areaCodeList;

    @ApiModelProperty(name = "polymerizeWay", value = "聚合方式 1:按月聚合 2:按日聚合")
    private String polymerizeWay;

    @ApiModelProperty(name = "nodeName", value = "节点名称")
    private String nodeName;

    @ApiModelProperty(name = "provinceCode", value = "省份代码")
    private String provinceCode;

    @ApiModelProperty(name = "cityCode", value = "市级代码")
    private String cityCode;

    @ApiModelProperty(name = "storeName", value = "服务店名称")
    private String storeName;

    @ApiModelProperty(name = "sort", value = "排序标志 ")
    private String sort;

    @ApiModelProperty(name = "complaintType", value = "投诉类型")
    private String complaintType;

    @ApiModelProperty(name = "productCategoryCode", value = "大区产品品类集合")
    private List<String> productCategoryCode;

    @ApiModelProperty(name = "productTypeCode", value = "大区产品类型集合")
    private List<String> productTypeCode;

    @ApiModelProperty(name = "accountingCenterCode", value = "核算中心code")
    private String accountingCenterCode;

}
