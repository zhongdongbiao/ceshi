package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.ProductInformation;

import java.util.List;

/**
 * 产品资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface ProductInformationDao {
    /**
     *产品资料删除
     */
    int batchProductInformationDelete();
    /**
     *产品资料添加
     * @param list
     */
    int batchProductInformation(List<ProductInformation> list);

}
