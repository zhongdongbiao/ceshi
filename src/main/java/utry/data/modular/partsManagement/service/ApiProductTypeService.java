package utry.data.modular.partsManagement.service;
import utry.data.modular.partsManagement.model.ProductType;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface ApiProductTypeService {
    /**
     * 删除旧产品类型数据
     */
    int batchDelete();
    /**
     * 添加新产品类型数据
     * @param list
     */
    int batchProductTypeData(List<ProductType> list);
}
