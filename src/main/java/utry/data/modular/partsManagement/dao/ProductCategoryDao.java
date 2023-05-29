package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.ProductCategory;

import java.util.List;

/**
 * 产品品类数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:24
 */
@Mapper
public interface ProductCategoryDao{

    /**
     * 产品品类数据添加
     * @param productCategory
     */
    void insertProductCategory(ProductCategory productCategory);
    /**
     *产品类型批量删除
     */
    int batchDelete();
    /**
     *产品类型批量新增
     * @param list
     */
    int batchProductCategoryData(List<ProductCategory> list);

    /**
     * 获取所有产品品类数据
     * @return
     */
    @DS("git_adb")
    List<ProductCategory> getAllProductCategory();
}
