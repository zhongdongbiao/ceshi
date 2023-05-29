package utry.data.modular.partsManagement.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.partsManagement.dao.PartsInformationDao;
import utry.data.modular.partsManagement.dao.ProductCategoryDao;
import utry.data.modular.partsManagement.model.PartsInformation;
import utry.data.modular.partsManagement.model.ProductCategory;
import utry.data.modular.partsManagement.service.ApiPartsService;
import utry.data.modular.partsManagement.service.ApiProductCategoryService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品品类管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class ApiProductCategoryServiceImpl implements ApiProductCategoryService {

    @Resource
    private ProductCategoryDao productCategoryDao;

    @Override
    public int batchDelete() {
        return productCategoryDao.batchDelete();
    }

    @Override
    public int batchProductCategoryData(List<ProductCategory> list) {
        return productCategoryDao.batchProductCategoryData(list);
    }

}

