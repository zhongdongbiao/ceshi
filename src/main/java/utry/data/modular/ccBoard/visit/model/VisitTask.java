package utry.data.modular.ccBoard.visit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访任务
 *
 * @author zhongdongbiao
 * @date 2022/10/24 13:23
 */
@Data
public class VisitTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统状态
     */
    private String systemState;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 服务类别
     */
    private String serviceType;

    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 服务店编号
     */
    private String storeNumber;

    /**
     * 服务店名称
     */
    private String storeName;

    /**
     * 服务店级别
     */
    private String storeLevel;

    /**
     * 工程师编号
     */
    private String engineerId;

    /**
     * 上门时间
     */
    private String doortTime;

    /**
     * 完成时间
     */
    private String completeTime;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品系列
     */
    private String productSeries;

    /**
     * 产品型号
     */
    private String productModel;

    /**
     * 机器编号
     */
    private String machineNumber;


    /**
     *  核算中心
     */
    private String accountingCenter;

    /**
     *  核算大区
     */
    private String accountingRegional;

    /**
     * 拨打次数
     */
    private String dialNumber;

    /**
     * 核算片区
     */
    private String accountingArea;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;
}
