package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.ProductType;

import java.util.List;

/**
 * 产品类型
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface ProductTypeDao{

    /**
     *产品类型添加
     * @param productType
     */
    void insertProductType(ProductType productType);
    /**
     *产品类型批量删除
     */
    int batchDelete();
    /**
     *产品类型批量新增
     * @param list
     */
    int batchProductTypeData(List<ProductType> list);
}
