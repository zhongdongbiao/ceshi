package utry.data.modular.partsManagement.service;
import utry.data.modular.partsManagement.model.ProductCategory;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface ApiProductCategoryService {
    /**
     * 删除旧产品品类数据
     */
    int batchDelete();
    /**
     * 添加新产品品类数据
     * @param list
     */
    int batchProductCategoryData(List<ProductCategory> list);
}
