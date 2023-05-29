package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.model.LocationInformation;

import java.util.List;
import java.util.Map;

/**
 * 配货周期数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface LocationInformationDao {


    /**
     *库位资料数据批量删除
     */
    int batchDelete();
    /**
     *库位资料数据批量新增
     * @param list
     */
    int batchLocationInformationData(List<LocationInformation> list);

    /**
     * 获取所有库位资料
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库位资料
     */
    @DS("gits_sharding")
    List<Map<Object,Object>> getAllLocationInformation(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
