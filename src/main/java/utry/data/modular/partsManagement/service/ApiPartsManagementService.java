package utry.data.modular.partsManagement.service;

import utry.data.modular.partsManagement.model.*;

import java.util.List;

/**
 * @author zhongdongbiao
 * @date 2022/4/8 9:47
 */
public interface ApiPartsManagementService {

    /**
     * 删除旧配货周期数据
     */
    int batchDistributionCycleDelete();
    /**
     * 添加配货周期数据
     * @param list
     */
    int batchDistributionCycle(List<DistributionCycle> list);

    /**
     * 删除库位资料数据
     */
    int batchLocationInformationDelete();
    /**
     * 添加库位资料数据
     * @param list
     */
    int batchLocationInformation(List<LocationInformation> list);

    /**
     * 删除服务店收货单扫描明细资料
     */
    int batchScanDetailDelete();

    /**
     * 添加服务店收货单扫描明细资料
     * @param list
     */
    int batchScanDetail(List<ScanDetail> list);

    /**
     * 删除产品资料
     */
    int batchProductInformationDelete();

    /**
     * 添加产品资料
     * @param list
     */
    int batchProductInformation(List<ProductInformation> list);

    /**
     * 删除型号部件关联
     */
    int batchModelPartsDelete();

    /**
     * 添加型号部件关联
     * @param list
     */
    int batchModelParts(List<ModelParts> list);
}
