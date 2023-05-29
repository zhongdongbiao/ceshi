package utry.data.modular.partsManagement.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.partsManagement.dao.*;
import utry.data.modular.partsManagement.model.*;
import utry.data.modular.partsManagement.service.ApiPartsManagementService;

import javax.annotation.Resource;
import java.util.List;
/**
 * @author zhongdongbiao
 * @date 2022/4/8 9:47
 */
@Service
public class ApiPartsManagementServiceImpl implements ApiPartsManagementService {
    @Resource
    private DistributionCycleDao distributionCycleDataDao;
    @Resource
    private LocationInformationDao locationInformationDao;
    @Resource
    private ScanDetailDao scanDetailDao;
    @Resource
    private ProductInformationDao productInformationDao;
    @Resource
    private ModelPartsDao modelPartsDao;

    @Override
    public int batchDistributionCycleDelete() {
        return distributionCycleDataDao.batchDelete();
    }

    @Override
    public int batchDistributionCycle(List<DistributionCycle> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return distributionCycleDataDao.batchDistributionCycleData(list);
        }
        return 0;
    }

    @Override
    public int batchLocationInformationDelete() {
        return locationInformationDao.batchDelete();
    }

    @Override
    public int batchLocationInformation(List<LocationInformation> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return locationInformationDao.batchLocationInformationData(list);
        }
        return 0;
    }

    @Override
    public int batchScanDetailDelete() {
        return scanDetailDao.batchScanDetailDelete();
    }

    @Override
    public int batchScanDetail(List<ScanDetail> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return scanDetailDao.batchScanDetail(list);
        }
        return 0;
    }

    @Override
    public int batchProductInformationDelete() {
        return productInformationDao.batchProductInformationDelete();
    }

    @Override
    public int batchProductInformation(List<ProductInformation> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return productInformationDao.batchProductInformation(list);
        }
        return 0;
    }

    @Override
    public int batchModelPartsDelete() {
        return modelPartsDao.batchModelPartsDelete();
    }

    @Override
    public int batchModelParts(List<ModelParts> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return modelPartsDao.batchModelParts(list);
        }
        return 0;
    }

}
