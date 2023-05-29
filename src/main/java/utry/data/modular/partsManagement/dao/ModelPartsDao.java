package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.ModelParts;

import java.util.List;

/**
 * 型号部件对应资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface ModelPartsDao {
    /**
     *工厂资料删除
     */
    int batchModelPartsDelete();
    /**
     *工厂资料添加
     * @param list
     */
    int batchModelParts(List<ModelParts> list);
}
