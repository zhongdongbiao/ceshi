package utry.data.modular.settleManagement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/26 14:33
 */
@Data
public class SettleDataDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "action", value = "动作（新增、修改）")
    private String action;

    @ApiModelProperty(name = "settleId", value = "结算单号")
    private String settleId;

    @ApiModelProperty(name = "settleDate", value = "结算日期")
    private String settleDate;

    @ApiModelProperty(name = "businessDate", value = "业务日期")
    private String businessDate;

    @ApiModelProperty(name = "createDate", value = "创建时间")
    private String createDate;

    @ApiModelProperty(name = "province", value = "省份名称")
    private String province;

    @ApiModelProperty(name = "provinceCode", value = "省份代码")
    private String provinceCode;

    @ApiModelProperty(name = "businessId", value = "业务单号")
    private String businessId;

    @ApiModelProperty(name = "businessSource", value = "业务来源")
    private String businessSource;

    @ApiModelProperty(name = "dispatchingOrder", value = "派工单号")
    private String dispatchingOrder;

    @ApiModelProperty(name = "productCategory", value = "产品品类")
    private String productCategory;

    @ApiModelProperty(name = "productCategoryCode", value = "产品品类代码")
    private String productCategoryCode;

    @ApiModelProperty(name = "productType", value = "产品类型")
    private String productType;

    @ApiModelProperty(name = "productTypeCode", value = "产品类型代码")
    private String productTypeCode;

    @ApiModelProperty(name = "productModel", value = "产品型号")
    private String productModel;

    @ApiModelProperty(name = "serviceUnit", value = "服务台数")
    private Integer serviceUnit;

    @ApiModelProperty(name = "doorCost", value = "上门费")
    private Float doorCost;

    @ApiModelProperty(name = "artificialCost", value = "人工费")
    private Float artificialCost;

    @ApiModelProperty(name = "remoteCost", value = "远程费")
    private Float remoteCost;

    @ApiModelProperty(name = "excessiveCost", value = "超标准费")
    private Float excessiveCost;

    @ApiModelProperty(name = "repairCost", value = "拉修费")
    private Float repairCost;

    @ApiModelProperty(name = "authenticateCost", value = "鉴定费")
    private Float authenticateCost;

    @ApiModelProperty(name = "subsidyCost", value = "补贴费")
    private Float subsidyCost;

    @ApiModelProperty(name = "dismountCost", value = "拆装费")
    private Float dismountCost;

    @ApiModelProperty(name = "deliveryCost", value = "配送费")
    private Float deliveryCost;

    @ApiModelProperty(name = "recallCost", value = "召回费")
    private Float recallCost;

    @ApiModelProperty(name = "logisticCost", value = "物流费")
    private Float logisticCost;

    @ApiModelProperty(name = "rewardCost", value = "奖励费用")
    private Float rewardCost;

    @ApiModelProperty(name = "compensateCost", value = "赔偿费用")
    private Float compensateCost;

    @ApiModelProperty(name = "adjustCost", value = "调整费用")
    private Float adjustCost;

    @ApiModelProperty(name = "settleMoney", value = "应结金额")
    private Float settleMoney;

    @ApiModelProperty(name = "settleObjectType", value = "结算对象类型")
    private String settleObjectType;

    @ApiModelProperty(name = "settleObjectName", value = "结算对象名称")
    private String settleObjectName;

    @ApiModelProperty(name = "accountAreaCode", value = "核算片区代码")
    private String accountAreaCode;

    @ApiModelProperty(name = "accountArea", value = "核算片区")
    private String accountArea;

    @ApiModelProperty(name = "factoryAuditFlag", value = "工厂审核标志")
    private String factoryAuditFlag;

    @ApiModelProperty(name = "factoryAuditResult", value = "工厂审核结果")
    private String factoryAuditResult;

    @ApiModelProperty(name = "costType", value = "费用类型")
    private String costType;

    @ApiModelProperty(name = "costSubType", value = "费用子类型")
    private String costSubType;

    @ApiModelProperty(name = "serviceType", value = "服务类型")
    private String serviceType;

    @ApiModelProperty(name = "serviceSubType", value = "服务子类型")
    private String serviceSubType;

    @ApiModelProperty(name = "buySystemState", value = "购机发票系统状态")
    private String buySystemState;

    @ApiModelProperty(name = "maintainMode", value = "维修方式")
    private String maintainMode;

}
