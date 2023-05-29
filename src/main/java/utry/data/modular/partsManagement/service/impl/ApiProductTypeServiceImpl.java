package utry.data.modular.partsManagement.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.partsManagement.dao.ProductTypeDao;
import utry.data.modular.partsManagement.model.ProductType;
import utry.data.modular.partsManagement.service.ApiProductTypeService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品类型管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class ApiProductTypeServiceImpl implements ApiProductTypeService {

    @Resource
    private ProductTypeDao productTypeDao;

    @Override
    public int batchDelete() {
        return productTypeDao.batchDelete();
    }

    @Override
    public int batchProductTypeData(List<ProductType> list) {
        return productTypeDao.batchProductTypeData(list);
    }

}

