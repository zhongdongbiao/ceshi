package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.DistrictAccounting;

import java.util.List;

/**
 * 核算大区资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface DistrictAccountingDao{

    /**
     * 核算大区资料添加
     * @param districtAccounting
     */
    void insertDistrictAccounting(DistrictAccounting districtAccounting);

    /**
     * 查询所有大区资料
     * @param districtAccounting
     */
    @DS("git_adb")
    List<DistrictAccounting> selectDistrictAccounting(DistrictAccounting districtAccounting);

    /**
     * 删除所有大区资料
     */
    int delDistrictAccounting();

}
