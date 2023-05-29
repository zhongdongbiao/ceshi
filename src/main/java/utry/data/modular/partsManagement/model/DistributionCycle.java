package utry.data.modular.partsManagement.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 配货周期数据表
 *
 * @author zhongdongbiao
 * @date 2022/4/15 10:06
 */
@Data
public class DistributionCycle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;
    /**
     * 系统状态
     */
    private String systemState;
    /**
     * 服务店编号
     */
    private String storeNumber;
    /**
     * 服务店名称
     */
    private String storeName;
    /**
     * 核算中心
     */
    private String accountingCenter;
    /**
     * 星期一0不配货，1配货
     */
    private String mon;
    /**
     * 星期二0不配货，1配货
     */
    private String tue;
    /**
     * 星期三0不配货，1配货
     */
    private String wed;
    /**
     * 星期四0不配货，1配货
     */
    private String thurs;
    /**
     * 星期五0不配货，1配货
     */
    private String fri;
    /**
     * 星期六0不配货，1配货
     */
    private String sta;
    /**
     * 星期日 0不配货，1配货
     */
    private String sun;

}
