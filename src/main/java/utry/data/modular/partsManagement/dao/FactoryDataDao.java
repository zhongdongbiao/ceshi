package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.FactoryData;

import java.util.List;

/**
 * 工厂资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface FactoryDataDao{

    /**
     *工厂资料添加
     * @param factoryData
     */
    void insertFactoryData(FactoryData factoryData);
    /**
     *工厂资料批量删除
     */
    int batchDelete();
    /**
     *工厂资料批量新增
     * @param list
     */
    int batchFactoryData(List<FactoryData> list);
}
