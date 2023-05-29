package utry.data.modular.partsManagement.service;

import utry.data.modular.partsManagement.model.FactoryData;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface ApiFactoryService {
    /**
     * 删除旧工厂数据
     */
    int batchDelete();
    /**
     * 添加新工厂数据
     * @param list
     */
    int batchFactoryData(List<FactoryData> list);
}
