package utry.data.modular.partsManagement.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.partsManagement.dao.FactoryDataDao;
import utry.data.modular.partsManagement.dao.PartsInformationDao;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.PartsInformation;
import utry.data.modular.partsManagement.service.ApiPartsService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部件资料管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class ApiPartsServiceImpl implements ApiPartsService {

    @Resource
    private PartsInformationDao partsInformationDao;

    @Override
    public int batchDelete() {
        return partsInformationDao.batchDelete();
    }

    @Override
    public int batchPartsData(List<PartsInformation> list) {
        return partsInformationDao.batchPartsData(list);
    }

}

