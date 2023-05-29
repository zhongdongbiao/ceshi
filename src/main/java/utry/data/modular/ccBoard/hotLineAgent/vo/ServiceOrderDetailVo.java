package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 人力数据图表-时段在线及工时表格-热线服务单明细表格-热线服务单详情视图
 * @author: WangXinhao
 * @create: 2022-10-21 11:15
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderDetailVo {

    /**
     * 客户评价
     */
    private String customerEvaluation;

    /**
     * 热线服务单
     */
    private String hotlineServiceOrderNo;

    /**
     * 记录来源
     */
    private String recordSource;

    /**
     * 核算中心
     */
    private String accountingCenter;

    /**
     * 客户编号
     */
    private String customerNo;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 关联派工单
     */
    private String dispatchingOrder;

    /**
     * 地区
     */
    private String city;

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
    private String storeRank;

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
     * 来电描述
     */
    private String describe;

    /**
     * 录音文件名
     */
    private String soundRecordFileName;

}
