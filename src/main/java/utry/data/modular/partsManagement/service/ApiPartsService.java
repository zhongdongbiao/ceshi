package utry.data.modular.partsManagement.service;

import utry.data.modular.partsManagement.model.PartsInformation;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface ApiPartsService {
    /**
     * 删除旧部件资料数据
     */
    int batchDelete();
    /**
     * 添加新部件资料数据
     * @param list
     */
    int batchPartsData(List<PartsInformation> list);
}
