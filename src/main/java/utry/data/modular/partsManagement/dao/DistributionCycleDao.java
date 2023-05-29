package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.model.DistributionCycle;

import java.util.List;

/**
 * 配货周期数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface DistributionCycleDao {


    /**
     *配货周期数据批量删除
     */
    int batchDelete();
    /**
     *配货周期数据批量新增
     * @param list
     */
    int batchDistributionCycleData(List<DistributionCycle> list);

    /**
     * 根据服务店编号和核算中心获取配货周期
     * @return
     */
    List<DistributionCycle> getDistributionStateByCondition();
}
