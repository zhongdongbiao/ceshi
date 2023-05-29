package utry.data.modular.partsManagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ProductInformation {
    /**
     * 产品型号
     */
    private String productModel;
    /**
     * 系统状态
     */
    private String systemState;
    /**
     * 产品品类
     */
    private String productCategory;
    /**
     * 产品类型代码
     */
    private String productCategoryCode;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 产品品类代码
     */
    private String productTypeCode;
    /**
     * 产品系列
     */
    private String productSeries;
    /**
     * 产品系列代码
     */
    private String productSeriesCode;
    /**
     * 品质反馈
     */
    private String qualityFeedback;
    /**
     * 品质反馈期限
     */
    private String qualityFeedbackDeadline;
    /**
     * 上市日期
     */
    private String listingDate;
    /**
     * 工厂名称
     */
    private String factoryName;
    /**
     * 工厂代码
     */
    private String factoryCode;
    /**
     * 新品说明书上传日期
     */
    private String manualTime;
    /**
     * 新品维修手册上传日期
     */
    private String serviceManualTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;
}
