package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.PartsInformation;

import java.util.List;

/**
 * 部件资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface PartsInformationDao{

    /**
     *部件资料添加
     * @param partsInformation
     */
    void insertPartsInformation(PartsInformation partsInformation);
    /**
     *部件资料批量删除
     */
    int batchDelete();
    /**
     *部件资料批量新增
     * @param list
     */
    int batchPartsData(List<PartsInformation> list);
}
